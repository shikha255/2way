package com.aistream.websocket;

import com.aistream.clients.ElevenLabsClient;
import com.aistream.model.Persona;
import com.aistream.clients.OpenAIClient;
import com.aistream.service.PersonaService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.ByteBuffer;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private final OpenAIClient openAIClient;
    private final ElevenLabsClient elevenLabsClient;
    private final PersonaService personaService;

    public AudioWebSocketHandler(
            OpenAIClient openAIClient,
            ElevenLabsClient elevenLabsClient,
            PersonaService personaService
    ) {
        this.openAIClient = openAIClient;
        this.elevenLabsClient = elevenLabsClient;
        this.personaService = personaService;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Persona persona = personaService.getDefaultPersona();
        byte[] dummyAudio = new byte[0];
        Mono<String> transcriptMono = openAIClient.mockTranscription(dummyAudio);

        transcriptMono
                .flatMapMany(transcript -> {
                    if (transcript.isBlank()) return Flux.empty();
                    return openAIClient.mockStreamResponse(transcript);
                })
                .flatMap(token -> {
                    // Convert each LLM token to audio using TTS with persona
                    // return elevenLabsClient.textToSpeech(token, persona)
                    return elevenLabsClient.mockTextToSpeech(token).flux();
                })
                .flatMap(audioBytes -> {
                    if (!session.isOpen()) return Mono.empty();
                    try {
                        session.sendMessage(new BinaryMessage(ByteBuffer.wrap(audioBytes)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        null,
                        Throwable::printStackTrace
                );
    }
}