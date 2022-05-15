package com.playlistconverter.converter;

import com.playlistconverter.converter.config.AppleMusicConfig;
import com.playlistconverter.converter.config.SpotifyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({SpotifyConfig.class, AppleMusicConfig.class})
@SpringBootApplication
public class PlaylistConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaylistConverterApplication.class, args);
    }

}
