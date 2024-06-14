package com.nhn.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dbynglvwk");
        config.put("api_key", "395933579618484");
        config.put("api_secret", "eCWgoHr0c8xicP8GoNUmT9g9-g0");
        return new Cloudinary(config);
    }
}
