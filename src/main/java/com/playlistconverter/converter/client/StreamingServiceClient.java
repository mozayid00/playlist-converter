package com.playlistconverter.converter.client;

import com.playlistconverter.converter.domain.Song;

import java.util.List;

public interface StreamingServiceClient {
    List<Song> getSongsFromPlaylist(String url);

    String getMatchingSongID(Song song);

    String createPlaylist(String playlistName, List<String> songIDs);

    String getPlaylistName(String url);

}
