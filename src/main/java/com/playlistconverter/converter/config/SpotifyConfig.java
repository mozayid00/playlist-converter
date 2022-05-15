package com.playlistconverter.converter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.spotify")
public class SpotifyConfig {
    private String clientId;
    private String clientSecret;
    private String userScopedRefreshToken;
    private String userId;
    private String basicAuthorization;

    public String getBasicAuthorization() {
        return basicAuthorization;
    }

    public void setBasicAuthorization(String basicAuthorization) {
        this.basicAuthorization = basicAuthorization;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUserScopedRefreshToken() {
        return userScopedRefreshToken;
    }

    public void setUserScopedRefreshToken(String userScopedRefreshToken) {
        this.userScopedRefreshToken = userScopedRefreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
