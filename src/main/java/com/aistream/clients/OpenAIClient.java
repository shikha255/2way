package com.aistream.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class OpenAIClient {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAIClient(
            WebClient.Builder builder,
            @Value("${openai.api-key}") String apiKey
    ) {
        this.webClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    /* ============================
       LLM — Streaming Chat
       ============================ */

    public Flux<String> streamChatCompletion(String requestJson) {
        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(this::extractToken);
    }

    private Mono<String> extractToken(String event) {
        if (event == null || event.contains("[DONE]")) {
            return Mono.empty();
        }
        try {
            int idx = event.indexOf("\"content\":\"");
            if (idx != -1) {
                int start = idx + 10;
                int end = event.indexOf("\"", start);
                return Mono.just(event.substring(start, end));
            }
        } catch (Exception ignored) {}
        return Mono.empty();
    }

    /* ============================
       STT — Whisper
       ============================ */

    public Mono<String> transcribeWav(byte[] wavAudio) {

        ByteArrayResource audioFile = new ByteArrayResource(wavAudio) {
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", audioFile);
        form.add("model", "whisper-1");

        return webClient.post()
                .uri("/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(form))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractText)
                .onErrorReturn("");
    }

    private String extractText(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return node.path("text").asText("");
        } catch (Exception e) {
            return "";
        }
    }

    public Mono<String> mockTranscription(byte[] audioChunk) {
        // You can ignore the actual bytes and just return a simulated string
        return Mono.just("This is a mocked transcription")
                .delayElement(Duration.ofMillis(200)); // simulate processing
    }

    /**
     * Mock LLM streaming response (for testing without OpenAI API)
     * @param userText The user input text
     * @return Flux<String> emitting tokens
     */
    public Flux<String> mockStreamResponse(String userText) {
        String response = "This is a mocked LLM response for testing purposes.";
        String[] tokens = response.split(" ");
        return Flux.fromArray(tokens)
                .delayElements(Duration.ofMillis(150)); // simulate streaming delay
    }
}