package com.aistream.websocket;

import com.aistream.model.Persona;
import com.aistream.service.PersonaService;
import com.aistream.service.StreamingLLMService;
import com.aistream.service.StreamingSTTService;
import com.aistream.service.StreamingTTSService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private final StreamingSTTService sttService;
    private final StreamingLLMService llmService;
    private final StreamingTTSService ttsService;
    private final PersonaService personaService;

    public AudioWebSocketHandler(
            StreamingSTTService sttService,
            StreamingLLMService llmService,
            StreamingTTSService ttsService,
            PersonaService personaService
    ) {
        this.sttService = sttService;
        this.llmService = llmService;
        this.ttsService = ttsService;
        this.personaService = personaService;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {

        Persona persona = personaService.getDefaultPersona();
        sttService.addAudioChunk(message.getPayload().array());

        sttService.processBufferedAudio()
                .flatMapMany(text -> llmService.streamResponse(persona, text))
                .flatMap(token -> ttsService.streamAudio(token, persona))
                .flatMap(audioBytes -> {
                    if (!session.isOpen()) return Mono.empty();
                    try {
                        session.sendMessage(new BinaryMessage(audioBytes));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                })
                .subscribe();
    }
}