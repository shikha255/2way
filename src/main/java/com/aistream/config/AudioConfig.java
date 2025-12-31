package com.aistream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "audio")
public class AudioConfig {

    private int sampleRate;
    private int chunkMs;

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChunkMs() {
        return chunkMs;
    }

    public void setChunkMs(int chunkMs) {
        this.chunkMs = chunkMs;
    }
}