package com.playlistconverter.converter.client.spotify.pojos;

public class GetSearchForItemResponse {
    private SearchTracksResponse tracks;

    public SearchTracksResponse getTracks() {
        return tracks;
    }

    public void setTracks(SearchTracksResponse tracks) {
        this.tracks = tracks;
    }
}
