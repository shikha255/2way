package com.aistream.service;

import com.aistream.clients.ElevenLabsClient;
import com.aistream.model.Persona;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class StreamingTTSService {

    private final ElevenLabsClient client;

    public StreamingTTSService(ElevenLabsClient client) {
        this.client = client;
    }

    public Mono<byte[]> streamAudio(String text, Persona persona) {
        return client.textToSpeech(text, persona);
    }

    /** MOCK */
    public Flux<byte[]> mockAudio() {
        byte[] wavHeader = "RIFF".getBytes();
        return Flux.just(wavHeader).delayElements(Duration.ofMillis(200));
    }

    private Flux<byte[]> split(byte[] bytes) {
        return Flux.generate(
                () -> 0,
                (i, sink) -> {
                    int chunk = 1024;
                    if (i >= bytes.length) {
                        sink.complete();
                        return i;
                    }
                    int end = Math.min(i + chunk, bytes.length);
                    byte[] part = new byte[end - i];
                    System.arraycopy(bytes, i, part, 0, end - i);
                    sink.next(part);
                    return end;
                }
        );
    }
}