package com.playlistconverter.converter.client.spotify.pojos;

import java.util.List;

public class GetPlaylistTracksResponse {
    private List <SpotifyItem> items;

    public List<SpotifyItem> getItems() {
        return items;
    }

    public void setItems(List<SpotifyItem> items) {
        this.items = items;
    }
}
