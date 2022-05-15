package com.playlistconverter.converter.client.applemusic.pojos.getplaylist;

import java.util.ArrayList;
import java.util.List;

public class GetLibraryPlaylistsResponse {
    private List<LibraryPlaylist> data = new ArrayList<>();

    public List<LibraryPlaylist> getData() {
        return data;
    }

    public void setData(List<LibraryPlaylist> data) {
        this.data = data;
    }
}
