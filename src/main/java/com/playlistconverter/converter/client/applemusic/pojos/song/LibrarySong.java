package com.playlistconverter.converter.client.applemusic.pojos.song;

public class LibrarySong {
    private String id;
    private LibrarySongAttributes attributes;
    private LibrarySongRelationships relationships;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LibrarySongAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LibrarySongAttributes attributes) {
        this.attributes = attributes;
    }

    public LibrarySongRelationships getRelationships() {
        return relationships;
    }

    public void setRelationships(LibrarySongRelationships relationships) {
        this.relationships = relationships;
    }
}
