package com.aistream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elevenlabs")
public class ElevenLabsConfig {

    private String apiKey;
    private String baseUrl = "https://api.elevenlabs.io/v1";
    private String defaultVoice;

    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDefaultVoice() {
        return defaultVoice;
    }
    public void setDefaultVoice(String defaultVoice) {
        this.defaultVoice = defaultVoice;
    }
}