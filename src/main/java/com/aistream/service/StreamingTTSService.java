package com.aistream.service;

import com.aistream.clients.ElevenLabsClient;
import com.aistream.model.Persona;
import com.aistream.utils.WavUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class StreamingTTSService {

    private static final boolean MOCK = true;

    private final ElevenLabsClient client;

    public StreamingTTSService(ElevenLabsClient client) {
        this.client = client;
    }

    public Flux<byte[]> streamAudio(String text, Persona persona) {
        if (MOCK) {
            return mockAudio(); // FULL WAV
        }
        return client.textToSpeech(text, persona).flux(); // FULL WAV
    }

    public Flux<byte[]> mockAudio() {
        // Generate a 1-second 440Hz sine wave WAV
        int sampleRate = 16000;
        double durationSec = 1.0;
        double frequency = 440.0;
        int numSamples = (int) (sampleRate * durationSec);
        byte[] pcm = new byte[numSamples * 2]; // 16-bit PCM
        for (int i = 0; i < numSamples; i++) {
            short val = (short) (Math.sin(2 * Math.PI * frequency * i / sampleRate) * Short.MAX_VALUE);
            pcm[i * 2] = (byte) (val & 0xFF);
            pcm[i * 2 + 1] = (byte) ((val >> 8) & 0xFF);
        }
        byte[] wav = WavUtil.wrapPcm(pcm); // Make proper WAV header
        return Flux.just(wav).delayElements(Duration.ofMillis(200));
    }
}