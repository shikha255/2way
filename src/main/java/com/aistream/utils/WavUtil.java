package com.aistream.utils;

public class WavUtil {

    public static byte[] wrapPcm(byte[] pcm) {
        int sampleRate = 16000;
        int byteRate = sampleRate * 2;

        byte[] header = new byte[]{
                'R','I','F','F',
                (byte)(36 + pcm.length),0,0,0,
                'W','A','V','E',
                'f','m','t',' ',
                16,0,0,0,
                1,0,
                1,0,
                (byte) sampleRate, (byte)(sampleRate >> 8),0,0,
                (byte) byteRate, (byte)(byteRate >> 8),0,0,
                2,0,
                16,0,
                'd','a','t','a',
                (byte) pcm.length,0,0,0
        };

        byte[] wav = new byte[header.length + pcm.length];
        System.arraycopy(header, 0, wav, 0, header.length);
        System.arraycopy(pcm, 0, wav, header.length, pcm.length);
        return wav;
    }
}