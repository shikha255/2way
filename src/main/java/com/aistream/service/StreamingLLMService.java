package com.aistream.service;

import com.aistream.clients.OpenAIClient;
import com.aistream.model.Persona;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class StreamingLLMService {

    private static final boolean MOCK = true;

    private final OpenAIClient openAIClient;

    public StreamingLLMService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public Flux<String> streamResponse(Persona persona, String userText) {
        if (MOCK) {
            return mockStreamResponse();
        }

        String json = """
        {
          "model":"gpt-4o-mini",
          "stream":true,
          "messages":[
            {"role":"system","content":"%s"},
            {"role":"user","content":"%s"}
          ]
        }
        """.formatted(
                persona.getSystemPrompt(),
                userText.replace("\"", "\\\"")
        );

        return openAIClient.streamChatCompletion(json);
    }

    public Flux<String> mockStreamResponse() {
        String response = "Hello! This is a mocked AI response streaming to you.";
        String[] tokens = response.split(" ");

        // Emit one token every 200ms
        return Flux.fromArray(tokens)
                .delayElements(Duration.ofMillis(200));
    }
}