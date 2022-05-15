package com.playlistconverter.converter.client.applemusic.pojos.createplaylist;

import com.playlistconverter.converter.client.applemusic.pojos.song.LibrarySong;

import java.util.List;

public class Data {
        private List<LibrarySong> librarySongList;

    public List<LibrarySong> getLibrarySongList() {
        return librarySongList;
    }

    public void setLibrarySongList(List<LibrarySong> librarySongList) {
        this.librarySongList = librarySongList;
    }
}
