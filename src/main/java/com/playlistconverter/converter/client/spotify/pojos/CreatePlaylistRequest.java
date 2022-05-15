package com.playlistconverter.converter.client.spotify.pojos;

public class CreatePlaylistRequest {
    private String name;
    private String description;
    private boolean isPublic;

    public String getName() {
        return name;
    }

    public CreatePlaylistRequest(String name, String description, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
