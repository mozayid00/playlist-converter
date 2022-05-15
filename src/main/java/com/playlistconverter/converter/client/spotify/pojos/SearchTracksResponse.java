package com.playlistconverter.converter.client.spotify.pojos;

import java.util.List;

public class SearchTracksResponse {
   private List<SpotifyTrack> items;

   public List<SpotifyTrack> getItems() {
      return items;
   }

   public void setItems(List<SpotifyTrack> items) {
      this.items = items;
   }
}
