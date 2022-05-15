package com.playlistconverter.converter.client.applemusic.pojos.getplaylist;

public class LibraryPlaylist {
    private String id;
    private LibraryPlaylistAttributes attributes;
    private LibraryPlaylistRelationships relationships;

    public LibraryPlaylistAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LibraryPlaylistAttributes attributes) {
        this.attributes = attributes;
    }

    public LibraryPlaylistRelationships getRelationships() {
        return relationships;
    }

    public void setRelationships(LibraryPlaylistRelationships relationships) {
        this.relationships = relationships;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
