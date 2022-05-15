package com.playlistconverter.converter.client.applemusic;

import com.playlistconverter.converter.client.StreamingServiceClient;
import com.playlistconverter.converter.client.applemusic.pojos.PlaylistUrlPathParams;
import com.playlistconverter.converter.client.applemusic.pojos.artists.Artists;
import com.playlistconverter.converter.client.applemusic.pojos.catalogsearch.CatalogSong;
import com.playlistconverter.converter.client.applemusic.pojos.catalogsearch.SearchResponse;
import com.playlistconverter.converter.client.applemusic.pojos.createplaylist.*;
import com.playlistconverter.converter.client.applemusic.pojos.getplaylist.GetLibraryPlaylistsResponse;
import com.playlistconverter.converter.client.applemusic.pojos.getplaylist.LibraryPlaylist;
import com.playlistconverter.converter.client.applemusic.pojos.song.LibrarySong;
import com.playlistconverter.converter.config.AppleMusicConfig;
import com.playlistconverter.converter.domain.Song;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The AppleMusic client implements the Streaming service client, makes use of interface methods to facilitate the
 * retrieval of playlist/song information, as well as the creation of playlist and adding of songs.
 */
@Component
public class AppleMusicClient implements StreamingServiceClient {
    private AppleMusicConfig appleMusicConfig;
    public AppleMusicClient(
            AppleMusicConfig appleMusicConfig){
        this.appleMusicConfig = appleMusicConfig;
    }
    /**
     * Takes url of an Apple Music playlist, parses the url for Storefront and id, uses them to make a GET request
     * to Apple Music API for songs in playlist, then maps them to a list of songs which it returns for comparison to other clients
     *
     * @param url Url to Apple Music playlist
     * @return List of song POJOs
     */
    @Override
    public List<Song> getSongsFromPlaylist(String url) {
        RestTemplate restTemplate = new RestTemplate();
        PlaylistUrlPathParams playlistUrlPathParams = getPlaylistPathParams(url);
        String requestEndpoint = String.format("https://api.music.apple.com/v1/catalog/%s/playlists/%s", playlistUrlPathParams.getStoreFront(), playlistUrlPathParams.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", generateToken()));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GetLibraryPlaylistsResponse> response = restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetLibraryPlaylistsResponse.class);
        LibraryPlaylist libraryPlaylist = response.getBody().getData().get(0);
        List<Song> songs = new ArrayList<>();
        addSongsToList(libraryPlaylist, songs);
        return songs;
        // API Endpoint: GET https://api.music.apple.com/v1/catalog/{storefront}/playlists/{id}

        // Documentation: https://developer.apple.com/documentation/applemusicapi/get_a_catalog_playlist


    }

    /**
     * Given playlist Url, parses for storefront and playlist id, and returns an object containing them
     *
     * @param url Url to Apple Music playlist
     * @return Object containing storefront and playlist id
     */
    private PlaylistUrlPathParams getPlaylistPathParams(String url) {
        String[] tokens = url.split("/");
        PlaylistUrlPathParams playlistUrlPathParams = new PlaylistUrlPathParams(tokens[3], tokens[tokens.length - 1]);
        return playlistUrlPathParams;
    }

    /**
     * Takes a Library Playlist JSON object, takes the Apple Music tracks which are stored in it, then takes their
     * values, and creates/returns a list of Song POJOs
     *
     * @param libraryPlaylist JSON object containing playlist info/relationships for given playlist
     * @param songs           List of song POJOs for each track in playlist
     */
    private void addSongsToList(LibraryPlaylist libraryPlaylist, List<Song> songs) {
        for (LibrarySong librarySong : libraryPlaylist.getRelationships().getTracks().getData()) {
            Song song = new Song();
            song.getArtistNames().add(librarySong.getAttributes().getArtistName());
            song.setAlbumName(librarySong.getAttributes().getAlbumName());
            song.setTitle(librarySong.getAttributes().getName());
            songs.add(song);
        }
    }

    @Override
    public String getMatchingSongID(Song song) {
        RestTemplate restTemplate = new RestTemplate();
        String query = constructQuery(song);

        String requestEndpoint = String.format("https://api.music.apple.com/v1/catalog/us/search?types=songs&term=%s", query);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", generateToken()));
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<SearchResponse> response = restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, SearchResponse.class);

        // If no results found, strip out album name and try again
        if (ObjectUtils.isEmpty(response.getBody().getResults().getSongs())) {
            String textToRemove = song.getAlbumName().replace(" ", "+");
            query = query.replace(textToRemove, "");
            requestEndpoint = String.format("https://api.music.apple.com/v1/catalog/us/search?types=songs&term=%s", query);
            response = restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, SearchResponse.class);
        }

        List<CatalogSong> catalogSongs = response.getBody().getResults().getSongs().getData();
        return findBestMatchingId(song, catalogSongs);

    }

    private String findBestMatchingId(Song song, List<CatalogSong> catalogSongs) {
        String matchingSongId = "";

        for (CatalogSong catalogSong : catalogSongs) {
            List<String> catalogSongArtistNames = new ArrayList<>();
            for (Artists catalogSongArtist : catalogSong.getRelationships().getArtists().getData()) {
                catalogSongArtistNames.add(catalogSongArtist.getAttributes().getName());
            }
            boolean titlesMatchEnough = false;
            if (catalogSong.getAttributes().getName().contains("feat.")) {
                if (catalogSong.getAttributes().getName().contains(song.getTitle())) {
                    titlesMatchEnough = true;
                }
            }
            if (song.getArtistNames().get(0).equalsIgnoreCase(catalogSong.getAttributes().getArtistName()) || titlesMatchEnough && song.getTitle().equals(catalogSong.getAttributes().getName())) {
                if (equalLists(song.getArtistNames(), catalogSongArtistNames))
                    matchingSongId = catalogSong.getId();

                if (catalogSong.getAttributes().getAlbumName().equals(song.getAlbumName())) {
                    System.out.printf("match found for: %s, %s %n", song.getArtistNames(), song.getTitle());
                    return matchingSongId;
                }
            }
        }
        if (matchingSongId == null) {
            System.out.printf("No match found for: %s, %s %n", song.getArtistNames(), song.getTitle());
        } else {
            System.out.printf("match found for: %s, %s %n", song.getArtistNames(), song.getTitle());
        }
        return matchingSongId;
    }

    private String constructQuery(Song song) {
        String query;
        StringBuilder stringBuilder = new StringBuilder(song.getTitle());
        stringBuilder.append(" ").append(song.getArtistNames().get(0));
        if (!ObjectUtils.isEmpty(song.getAlbumName())) {
            stringBuilder.append(" ").append(song.getAlbumName());
        }
        query = stringBuilder.toString();
        if (query.contains("&")) {
            query = query.replace("&", "");
        }
        if (query.contains(";")) {
            query = query.replace(";", "");
        }
        return query.replace(" ", "+");
    }


    @Override
    public String createPlaylist(String playlistName, List<String> songIDs) {
        RestTemplate restTemplate = new RestTemplate();
        String createPlaylistRequestEndpoint = "https://api.music.apple.com/v1/en/library/playlists";
        LibraryPlaylistCreationRequest createPlaylistPostBody = generateLibraryPlaylistCreationRequest(playlistName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", generateToken()));
        HttpEntity<LibraryPlaylistCreationRequest> libraryPlaylistCreationRequestHttpEntity = new HttpEntity<>(createPlaylistPostBody, headers);
        ResponseEntity<GetLibraryPlaylistsResponse> createPlaylistResponse = restTemplate.exchange(createPlaylistRequestEndpoint, HttpMethod.POST, libraryPlaylistCreationRequestHttpEntity, GetLibraryPlaylistsResponse.class);

        String playlistId = createPlaylistResponse.getBody().getData().get(0).getId();
        String addTracksRequestEndpoint = String.format("https://api.music.apple.com/v1/en/library/playlists/%s/tracks", playlistId);
        Tracks tracks = compileTrackData(songIDs);
        LibraryPlaylistTracksRequest addTracksPostBody = new LibraryPlaylistTracksRequest(tracks);
        HttpEntity<LibraryPlaylistTracksRequest> libraryPlaylistTracksRequestHttpEntity = new HttpEntity<>(addTracksPostBody, headers);
        ResponseEntity<String> libraryPlaylistTracksResponse = restTemplate.exchange(addTracksRequestEndpoint, HttpMethod.POST, libraryPlaylistTracksRequestHttpEntity, String.class);
        if (!ObjectUtils.isEmpty(libraryPlaylistTracksResponse)) {
            System.out.println("Tracks added successfully.");
        }
        return compileUrl(playlistName, playlistId);


    }

    private String compileUrl(String playlistName, String playlistId) {
        StringBuilder url = new StringBuilder("music.apple.com/us/playlist/");
        playlistName = playlistName.replace(' ', '-');
        url.append(playlistName);
        url.append("/").append(playlistId);
        return url.toString();
    }

    private Tracks compileTrackData(List<String> songIDs) {
        Tracks tracks = new Tracks();
        for (String songId : songIDs) {
            LibrarySong librarySong = new LibrarySong();
            librarySong.setId(songId);
        }
        return tracks;
    }

    private LibraryPlaylistCreationRequest generateLibraryPlaylistCreationRequest(String playlistName) {
        LibraryPlaylistCreationAttributes attributes = new LibraryPlaylistCreationAttributes();
        attributes.setName(playlistName);
        return new LibraryPlaylistCreationRequest(attributes);

    }

    @Override
    public String getPlaylistName(String url) {
        RestTemplate restTemplate = new RestTemplate();
        PlaylistUrlPathParams playlistUrlPathParams = getPlaylistPathParams(url);
        String requestEndpoint = String.format("https://api.music.apple.com/v1/catalog/%s/playlists/%s",
                playlistUrlPathParams.getStoreFront(), playlistUrlPathParams.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", generateToken()));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GetLibraryPlaylistsResponse> response = restTemplate.exchange(requestEndpoint, HttpMethod.GET, request, GetLibraryPlaylistsResponse.class);
        return response.getBody().getData().get(0).getAttributes().getName();


    }

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

    private String generateToken() {
        final int expiration = 1000 * 60 * 5;

        try {
            return Jwts.builder()
                    .setHeaderParam(JwsHeader.KEY_ID, appleMusicConfig.getKeyId())
                    .setHeaderParam(JwsHeader.ALGORITHM,"ES256")
                    .setIssuer(appleMusicConfig.getIssuer())
                    .setAudience("https://appleid.apple.com")
                    .setSubject(appleMusicConfig.getSubject())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                    .compact();
        } catch (IOException e) {
            return "";
        }
    }

    private PrivateKey getPrivateKey() throws IOException {
        Path filePath = Path.of(appleMusicConfig.getPrivateKeyLocation());
        final Reader pemReader = new StringReader(Files.readString(filePath));
        final PEMParser pemParser = new PEMParser(pemReader);
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo keyInfo = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(keyInfo);
    }
}

