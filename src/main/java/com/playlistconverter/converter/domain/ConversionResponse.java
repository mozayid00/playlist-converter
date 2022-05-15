package com.playlistconverter.converter.domain;

public class ConversionResponse {
    private String playlistUrl;

    public ConversionResponse(){

    }

    public ConversionResponse(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }
}
