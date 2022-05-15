package com.playlistconverter.converter.client.applemusic.pojos.catalogsearch;

import com.playlistconverter.converter.client.applemusic.pojos.artists.Artists;

import java.util.ArrayList;
import java.util.List;

public class CatalogSongArtistRelationship {
    List<Artists> data = new ArrayList<>();

    public List<Artists> getData() {
        return data;
    }

    public void setData(List<Artists> data) {
        this.data = data;
    }
}
