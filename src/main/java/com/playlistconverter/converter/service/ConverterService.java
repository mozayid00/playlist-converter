package com.playlistconverter.converter.service;

import com.playlistconverter.converter.client.applemusic.AppleMusicClient;
import com.playlistconverter.converter.client.spotify.SpotifyClient;
import com.playlistconverter.converter.client.StreamingServiceClient;
import com.playlistconverter.converter.domain.ConversionResponse;
import com.playlistconverter.converter.domain.Song;
import com.playlistconverter.converter.domain.StreamingServiceType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConverterService {
    private SpotifyClient spotifyClient;
    private AppleMusicClient appleMusicClient;

    public ConverterService(
            SpotifyClient spotifyClient,
            AppleMusicClient appleMusicClient
    ){
        this.spotifyClient = spotifyClient;
        this.appleMusicClient = appleMusicClient;
    }


    private StreamingServiceClient identifyStreamingServiceClient(StreamingServiceType streamingServiceType){
       return switch (streamingServiceType){
           case SPOTIFY -> spotifyClient;
           case APPLE_MUSIC -> appleMusicClient;
           default -> {
               throw new RuntimeException("Streaming service not supported. \n");
           }
        };
    }

    public ConversionResponse convertPlaylist(String url, StreamingServiceType startingService, StreamingServiceType destinationService){

        // 1. Identify starting service, select the corresponding client
        StreamingServiceClient startingClient = identifyStreamingServiceClient(startingService);

        // 2. Make request to startingService Client to retrieve song details for each song in playlist, and retrieve playlist name.
        List<Song> songsInPlaylist = startingClient.getSongsFromPlaylist(url);
        String playlistName = startingClient.getPlaylistName(url);

        // 3. Identify destination service, select the corresponding client
        StreamingServiceClient destinationClient = identifyStreamingServiceClient(destinationService);

        // 4. Send song details to destinationService Client to find matches for each song in playlist, and get platform specific ID for each
        List<String> songIDs = new ArrayList<>();
        for (Song song : songsInPlaylist){
            String matchingUri = destinationClient.getMatchingSongID(song);
            if (!ObjectUtils.isEmpty(matchingUri)) {
                songIDs.add(matchingUri);
            }
        }

        // 5. send song list to destination service client, and request to add each song to a user playlist.
       String playlistUrl = destinationClient.createPlaylist(playlistName, songIDs);
        return new ConversionResponse(playlistUrl);
    }
}
