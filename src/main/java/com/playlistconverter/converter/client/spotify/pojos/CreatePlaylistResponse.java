package com.playlistconverter.converter.client.spotify.pojos;

public class CreatePlaylistResponse {
    private String id;
    private SpotifyExternalUrls external_urls;

    public SpotifyExternalUrls getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(SpotifyExternalUrls external_urls) {
        this.external_urls = external_urls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
