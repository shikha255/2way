package com.aistream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "persona")
public class PersonaConfig {

    private String defaultPersona;

    public String getDefault() {
        return defaultPersona;
    }

    public void setDefault(String defaultPersona) {
        this.defaultPersona = defaultPersona;
    }
}