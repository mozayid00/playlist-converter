package com.playlistconverter.converter.client.youtubemusic;

import com.playlistconverter.converter.client.StreamingServiceClient;
import com.playlistconverter.converter.domain.Song;

import java.util.List;

public class YoutubeMusicClient implements StreamingServiceClient {

    @Override
    public List<Song> getSongsFromPlaylist(String url) {
        return null;
    }

    @Override
    public String getMatchingSongID(Song song) {
        return null;
    }

    @Override
    public String createPlaylist(String playlistName, List<String> songIDs) {
        return null;
    }

    @Override
    public String getPlaylistName(String url) {
        return null;
    }
}
