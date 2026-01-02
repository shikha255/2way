package com.aistream.clients;

import com.aistream.config.ElevenLabsConfig;
import com.aistream.model.Persona;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ElevenLabsClient {

    private final WebClient webClient;
    private final ElevenLabsConfig config;

    public ElevenLabsClient(WebClient.Builder webClientBuilder, ElevenLabsConfig config) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.elevenlabs.io/v1")
                .build();
        this.config = config;
    }

    /**
     * Converts text to speech using ElevenLabs API
     * Returns full audio bytes as Mono<byte[]>
     */
    public Mono<byte[]> textToSpeech(String text, Persona persona) {
        String voiceId = config.getDefaultVoice();

        String json = "{"
                + "\"text\":\"" + escapeJson(text) + "\","
                + "\"model_id\":\"eleven_multilingual_v2\","
                + "\"voice_settings\":{"
                + "\"stability\":" + persona.getStability() + ","
                + "\"similarity_boost\":" + persona.getStyle()
                + "}"
                + "}";

        return webClient.post()
                .uri("/text-to-speech/{voiceId}", voiceId)
                .header("xi-api-key", config.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(byte[].class)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.empty();
                });
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }
}