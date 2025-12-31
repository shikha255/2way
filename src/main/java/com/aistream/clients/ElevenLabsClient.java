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

    /**
     * Mock TTS: generates a 1-second 440Hz sine wave WAV
     */
    public Mono<byte[]> mockTextToSpeech(String text) {
        int sampleRate = 16000;
        double durationSec = 1.0;
        double frequency = 440.0; // A4
        int numSamples = (int) (sampleRate * durationSec);

        byte[] audioData = new byte[numSamples * 2]; // 16-bit PCM
        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            short sample = (short) (Math.sin(angle) * Short.MAX_VALUE);
            audioData[i * 2] = (byte) (sample & 0xff);
            audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        // WAV header
        byte[] wavHeader = createWavHeader(audioData.length, sampleRate, 1, 16);

        byte[] wavFile = new byte[wavHeader.length + audioData.length];
        System.arraycopy(wavHeader, 0, wavFile, 0, wavHeader.length);
        System.arraycopy(audioData, 0, wavFile, wavHeader.length, audioData.length);

        return Mono.just(wavFile);
    }

    private byte[] createWavHeader(int audioDataLength, int sampleRate, int channels, int bitsPerSample) {
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataSize = audioDataLength;

        byte[] header = new byte[44];
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        int chunkSize = 36 + dataSize;
        header[4] = (byte) (chunkSize & 0xff);
        header[5] = (byte) ((chunkSize >> 8) & 0xff);
        header[6] = (byte) ((chunkSize >> 16) & 0xff);
        header[7] = (byte) ((chunkSize >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0;
        header[20] = 1; header[21] = 0;
        header[22] = (byte) channels; header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) blockAlign; header[33] = 0;
        header[34] = (byte) bitsPerSample; header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (dataSize & 0xff);
        header[41] = (byte) ((dataSize >> 8) & 0xff);
        header[42] = (byte) ((dataSize >> 16) & 0xff);
        header[43] = (byte) ((dataSize >> 24) & 0xff);
        return header;
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }
}