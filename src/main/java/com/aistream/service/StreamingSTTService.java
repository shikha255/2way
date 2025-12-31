package com.aistream.service;

import com.aistream.clients.OpenAIClient;
import com.aistream.utils.WavUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class StreamingSTTService {

    private final OpenAIClient openAIClient;
    private final List<byte[]> audioBuffer = new ArrayList<>();

    public StreamingSTTService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public synchronized void addAudioChunk(byte[] chunk) {
        audioBuffer.add(chunk);
    }

    public Mono<String> processBufferedAudio() {
        byte[] pcm;

        synchronized (this) {
            if (audioBuffer.isEmpty()) {
                return Mono.just("");
            }

            int size = audioBuffer.stream().mapToInt(b -> b.length).sum();
            pcm = new byte[size];

            int offset = 0;
            for (byte[] c : audioBuffer) {
                System.arraycopy(c, 0, pcm, offset, c.length);
                offset += c.length;
            }
            audioBuffer.clear();
        }

        byte[] wav = WavUtil.wrapPcm(pcm);
        return openAIClient.transcribeWav(wav);
    }
}