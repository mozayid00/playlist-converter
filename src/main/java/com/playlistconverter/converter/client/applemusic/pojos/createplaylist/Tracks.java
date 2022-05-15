package com.playlistconverter.converter.client.applemusic.pojos.createplaylist;

import com.playlistconverter.converter.client.applemusic.pojos.song.LibrarySong;

import java.util.ArrayList;
import java.util.List;

public class Tracks {
    private List<LibrarySong> data = new ArrayList<>();

    public List<LibrarySong> getData() {
        return data;
    }

    public void setData(List<LibrarySong> data) {
        this.data = data;
    }
}
