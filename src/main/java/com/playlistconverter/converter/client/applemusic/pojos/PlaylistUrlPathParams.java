package com.playlistconverter.converter.client.applemusic.pojos;

public class PlaylistUrlPathParams {
    private String storeFront;
    private String id;

    public PlaylistUrlPathParams(String storeFront, String id) {
        this.storeFront = storeFront;
        this.id = id;
    }

    public String getStoreFront() {
        return storeFront;
    }

    public void setStoreFront(String storeFront) {
        this.storeFront = storeFront;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
