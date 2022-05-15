package com.playlistconverter.converter.client.spotify;

import com.playlistconverter.converter.client.StreamingServiceClient;
import com.playlistconverter.converter.client.spotify.pojos.*;
import com.playlistconverter.converter.config.SpotifyConfig;
import com.playlistconverter.converter.domain.Song;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * The Spotify client implements the Streaming service client, makes use of interface methods to facilitate the
 retrieval of playlist/song information, as well as the creation of playlist and adding of songs.
 */
@Component
public class SpotifyClient implements StreamingServiceClient {

    private SpotifyConfig spotifyConfig;

    public SpotifyClient(
            SpotifyConfig spotifyConfig){
        this.spotifyConfig = spotifyConfig;
    }


    /**
     * Calls the parsePlaylistIdFromURL method to retrieve playlistId
     * Calls GetPlaylistTracksResponse to return the list of songs as SpotifyTracks
     * Calls mapSpotifyTrackToSong to map Spotify Tracks to song POJO allowing the values to be directly compared
     * to those on any other client.
     * @param url
     * @return songs The songs which we wish to add to the playlist on the destination client.
     */
    @Override
    public List<Song> getSongsFromPlaylist(String url) {

        String playlistID = parsePlaylistIdFromURL(url);

        GetPlaylistTracksResponse response = requestPlaylistInformation(playlistID);

        List<Song> songs = new ArrayList<>();
        for (SpotifyItem item : response.getItems()) {
            songs.add(mapSpotifyTrackToSong(item.getTrack()));
        }

        return songs;
    }

    /**
     * Takes a SpotifyTrack object, and retrieves its title, artist name(s), and album name (if exists),
     * and assigns them to a newly created song POJO
     * @param track The actual Spotify Track and its corresponding data
     * @return The newly created song POJO whose values correspond to track
     */
    private Song mapSpotifyTrackToSong(SpotifyTrack track) {
        Song song = new Song();
        song.setTitle(track.getName());
        song.setAlbumName(track.getAlbum().getName());

        List<String> artistNames = new ArrayList<>();
        for (SpotifyArtist artist : track.getArtists()) {
            artistNames.add(artist.getName());
        }
        song.setArtistNames(artistNames);
        return song;
    }

    /**
     * Makes a GET request to Spotify API to retrieve playlist information such as playlist name and songs contained.
     * @param playlistID The id for the playlist whose information needs to be retrieved.
     * @return GetPlaylistTracksResponse a JSON object which contains all information about the playlist.
     */
    private GetPlaylistTracksResponse requestPlaylistInformation(String playlistID) {
        RestTemplate restTemplate = new RestTemplate();

        String requestEndpoint = String.format("https://api.spotify.com/v1/playlists/%s/tracks", playlistID);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", requestAccessToken()));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GetPlaylistTracksResponse> response =
                restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetPlaylistTracksResponse.class);
        return response.getBody();
    }

    /**
     * Uses song's values to create a query, and makes a get request to Spotify API to search for songs with song's
     * values, and returns a response which is a body of tracks whose values are potential matches for song.
     *
     * @param song The song object for which we'd like to find matches
     * @return GetSearchForItemResponse a JSON object containing SpotifyTracks that are potential matches
     */
    private GetSearchForItemResponse executeSongSearch(Song song) {
        String searchQuery = produceItemSearchQuery(song);

        RestTemplate restTemplate = new RestTemplate();

        String requestEndpoint = String.format("https://api.spotify.com/v1/search?q=%s&type=track", searchQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", requestAccessToken()));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GetSearchForItemResponse> response =
                restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetSearchForItemResponse.class);
        if (ObjectUtils.isEmpty(response.getBody().getTracks().getItems())){
            searchQuery = searchQuery.replace(song.getAlbumName(), "");
            requestEndpoint = String.format("https://api.spotify.com/v1/search?q=%s&type=track", searchQuery);
            response =
                    restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetSearchForItemResponse.class);



        }

        return response.getBody();
    }

    /**
     * Makes a GET request to Spotify API, returns name of playlist whose URL we give it
     * @param url Url to the playlist whose name we'd like to retrieve
     * @return name of playlist
     */
    public String getPlaylistName(String url) {
        String playlistId = parsePlaylistIdFromURL(url);
        RestTemplate restTemplate = new RestTemplate();

        String requestEndpoint = String.format("https://api.spotify.com/v1/playlists/%s", playlistId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", requestAccessToken()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GetPlaylistResponse> response =
                restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetPlaylistResponse.class);
        return response.getBody().getName();
    }

    /**
     * Takes the values of song, and adds them into one string to create a search query
     * @param song song which we want to make a query for
     * @return String that contains all of song's values (search query)
     */
    private String produceItemSearchQuery(Song song) {
        StringBuilder queryBuilder = new StringBuilder(song.getTitle());
        for (String name : song.getArtistNames()) {
            queryBuilder.append(" ").append(name);
        }

        if (!ObjectUtils.isEmpty(song.getAlbumName())) {
            queryBuilder.append(" ").append(song.getAlbumName());
        }

        return queryBuilder.toString();
    }

    /**
     * Splits the url by "/" and retrieves the id, which is a path parameter in the url.
     * @param url The entire url for accessing the playlist.
     * @return the id of the url, needed for making information requests.
     */
    private String parsePlaylistIdFromURL(String url) {
        String[] tokens = url.split("/");
        return tokens[tokens.length - 1];
    }

    /**
     * Calls the potentialItemMatchesMakes method to store potential matches for songToMatch
     * Creates a hashmap for the Spotify ID of potential matches, and their corresponding song POJOs
     * Calls mapSpotifyTrackToSong method to map the tracks to song POJOs
     * Calls the determineBestMatchingURI method to determine the id of the best match.
     * @param songToMatch The song which the method will find potential matches for.
     * @return xSongID the ID of the song whose values were the best match for songToMatch.
     */
    @Override
    public String getMatchingSongID(Song songToMatch) {
        List<SpotifyTrack> potentialItemMatches = executeSongSearch(songToMatch).getTracks().getItems();

        HashMap<String, Song> songsForIds = new HashMap<>(); // K = Spotify URI, V = Track Info
        for (SpotifyTrack track : potentialItemMatches) {
            songsForIds.put(track.getUri(), mapSpotifyTrackToSong(track));
        }

        return determineBestMatchingURI(songToMatch, songsForIds);
    }

    /**
     * Iterates through the potential matches and compares Title, artistNames, and albumName.
     * Title and artistName are required to be the same for a song to be considered a match.
     * All 3 fields being the same is considered an exact match.
     * @param song The original song which was passed to find matches for.
     * @param songsForIds A hashmap consisting of different songs found on Spotify and their respective ids.
     * @return matchingSongUri the uri of the Spotify Track whose values were the best match.
     */
    private String determineBestMatchingURI(Song song, HashMap<String, Song> songsForIds) {
        String matchingSongURI = null;
        boolean songsMatchEnough = false;

        for (Map.Entry<String, Song> entry : songsForIds.entrySet()) {
            if (entry.getValue().getTitle().contains("feat.")){
                if (entry.getValue().getTitle().contains(song.getTitle())){
                    songsMatchEnough = true;
                }
            }

            if (entry.getValue().getTitle().equalsIgnoreCase(song.getTitle()) || songsMatchEnough
                    && equalLists(entry.getValue().getArtistNames(), song.getArtistNames())) {

                matchingSongURI = entry.getKey();

                if (song.getAlbumName().equals(entry.getValue().getAlbumName())) {
                    return matchingSongURI;
                }
            }
        }

        if (matchingSongURI == null) {
            System.out.printf("No match found for track %s: %s. %n", song.getArtistNames(),
                    song.getTitle());

        }
        return matchingSongURI;
    }

    /**
     * The service makes a POST request to the Spotify API to create a playlist.
     * @param playlistName The name of the playlist from the original streaming service.
     * @param songIDs the ids of the matched songs that will be added to the playlist.

     *For adding tracks to playlist, a separate POST request is made with the id of the match in the postBody.
     * @return createPlayListResponse gives back information about the created playlist (name, href, url, id)
     */

    @Override
    public String createPlaylist(String playlistName, List<String> songIDs) {
        RestTemplate restTemplate = new RestTemplate();
        String requestEndpoint = String.format("https://api.spotify.com/v1/users/%s/playlists", spotifyConfig.getUserId());
        CreatePlaylistRequest postBody = new CreatePlaylistRequest(playlistName, " ", true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", refreshTokenAndGetAccessToken()));
        HttpEntity<CreatePlaylistRequest> createPlaylistRequestHttpEntity = new HttpEntity<>(postBody, headers);
        ResponseEntity<CreatePlaylistResponse> createPlaylistResponse = restTemplate.exchange(requestEndpoint, HttpMethod.POST, createPlaylistRequestHttpEntity, CreatePlaylistResponse.class);

        //Add songs request below
        for (String songId : songIDs) {
            String addTracksEndpoint = String.format("https://api.spotify.com/v1/playlists/%s/tracks?uris=%s", createPlaylistResponse.getBody().getId(), songId);
            HttpEntity<Void> addTracksRequest = new HttpEntity<>(headers);
            ResponseEntity<String> addTrackResponse = restTemplate.exchange(addTracksEndpoint, HttpMethod.POST, addTracksRequest, String.class);
            if (addTrackResponse.getStatusCode().isError()){
                System.out.println("Failed to add track. \n");
            }
        }
        return createPlaylistResponse.getBody().getExternal_urls().getSpotify();
    }


    /**
     * Contacts the Spotify API to retrieve access token to be used for subsequent API requests.
     *
     * @return access token for Spotify API requests
     */
    private String requestAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("grant_type", "client_credentials");
        data.add("client_id", spotifyConfig.getClientId());
        data.add("client_secret", spotifyConfig.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(data, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                TokenResponse.class
        );

        return response.getBody().getAccess_token();
    }

    /**
     * Provides a permanent user token and retrieves a termporary token for manipulating user assets (namely playlists)
     * @return the token needed for authorizing playlist changes and creation
     */
    private String refreshTokenAndGetAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(spotifyConfig.getBasicAuthorization());

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("grant_type", "refresh_token");
        data.add("refresh_token", spotifyConfig.getUserScopedRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(data, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                TokenResponse.class
        );

        return response.getBody().getAccess_token();
    }

    /**
     * Checks two lists two see if the values within are the same regardless of order.
     * @param one any given list of Strings
     * @param two any other given list of Strings
     * @return equalLists a boolean that returns true if the lists are the same.
     */
    private boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
}
