package com.playlistconverter.converter.client.applemusic.pojos.getplaylist;

import com.playlistconverter.converter.client.applemusic.pojos.createplaylist.Tracks;
import com.playlistconverter.converter.client.applemusic.pojos.song.LibrarySong;

import java.util.ArrayList;
import java.util.List;

public class LibraryPlaylistRelationships {
    private Tracks tracks;

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }
}
