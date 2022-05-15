package com.playlistconverter.converter.client.applemusic.pojos.createplaylist;

public class LibraryPlaylistTracksRequest {
    private Tracks tracks;

    public LibraryPlaylistTracksRequest(Tracks tracks) {
        this.tracks = tracks;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }
}
