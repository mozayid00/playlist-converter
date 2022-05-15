package com.playlistconverter.converter.client.applemusic.pojos.catalogsearch;

public class CatalogSong {
    private String id;
    private CatalogSongAttributes attributes;
    private CatalogSongRelationships relationships;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CatalogSongAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(CatalogSongAttributes attributes) {
        this.attributes = attributes;
    }

    public CatalogSongRelationships getRelationships() {
        return relationships;
    }

    public void setRelationships(CatalogSongRelationships relationships) {
        this.relationships = relationships;
    }
}
