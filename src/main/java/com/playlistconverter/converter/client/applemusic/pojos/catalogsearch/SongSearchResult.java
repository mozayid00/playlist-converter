package com.playlistconverter.converter.client.applemusic.pojos.catalogsearch;

import java.util.ArrayList;
import java.util.List;

public class SongSearchResult {
    private List<CatalogSong> data = new ArrayList<>();

    public List<CatalogSong> getData() {
        return data;
    }

    public void setData(List<CatalogSong> data) {
        this.data = data;
    }
}
