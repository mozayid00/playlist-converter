package com.playlistconverter.converter.client.applemusic.pojos.createplaylist;

public class LibraryPlaylistCreationRequest {
     private LibraryPlaylistCreationAttributes libraryPlaylistCreationAttributes;

    public LibraryPlaylistCreationRequest(LibraryPlaylistCreationAttributes libraryPlaylistCreationAttributes) {
        this.libraryPlaylistCreationAttributes = libraryPlaylistCreationAttributes;
    }

    public LibraryPlaylistCreationAttributes getLibraryPlaylistCreationAttributes() {
        return libraryPlaylistCreationAttributes;
    }

    public void setLibraryPlaylistCreationAttributes(LibraryPlaylistCreationAttributes libraryPlaylistCreationAttributes) {
        this.libraryPlaylistCreationAttributes = libraryPlaylistCreationAttributes;
    }
}
