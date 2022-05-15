package com.playlistconverter.converter.domain;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private String title;
    private List<String> artistNames = new ArrayList<>();
    private String albumName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
