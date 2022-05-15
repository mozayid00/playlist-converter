package com.playlistconverter.converter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.apple-music")
public class AppleMusicConfig {
    private String keyId;
    private String issuer;
    private String subject;
    private String privateKeyLocation;

    public String getPrivateKeyLocation() {
        return privateKeyLocation;
    }

    public void setPrivateKeyLocation(String privateKeyLocation) {
        this.privateKeyLocation = privateKeyLocation;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
