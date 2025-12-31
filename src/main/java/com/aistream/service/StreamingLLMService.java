package com.aistream.service;

import com.aistream.clients.OpenAIClient;
import com.aistream.model.Persona;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingLLMService {

    private final OpenAIClient openAIClient;

    public StreamingLLMService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public Flux<String> streamResponse(Persona persona, String userText) {

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
}