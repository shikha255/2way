package com.aistream.model;

public enum Persona {

    LIFE_COACH(
            "You are a calm, empathetic life coach. Speak slowly, warmly, and with reassurance.",
            "alloy",       // ElevenLabs voice ID
            0.45,          // stability: lower = more expressive
            0.75,          // style: higher = more emotional
            "coach_avatar" // optional avatar ID
    ),

    TECH_MENTOR(
            "You are a senior software engineer. Speak clearly and confidently. Give concise explanations.",
            "arcane",
            0.65,
            0.45,
            "tech_avatar"
    ),

    THERAPIST(
            "You are a compassionate therapist. Listen actively and provide thoughtful guidance.",
            "serene",
            0.4,
            0.8,
            "therapist_avatar"
    ),

    FRIENDLY_ASSISTANT(
            "You are a friendly, casual assistant. Keep the conversation light and cheerful.",
            "friendly_voice",
            0.6,
            0.7,
            "friendly_avatar"
    );

    // --------------------------
    // Fields
    // --------------------------
    private final String systemPrompt;
    private final String voice;
    private final double stability;
    private final double style;
    private final String avatarId;

    // --------------------------
    // Constructor
    // --------------------------
    Persona(String systemPrompt, String voice, double stability, double style, String avatarId) {
        this.systemPrompt = systemPrompt;
        this.voice = voice;
        this.stability = stability;
        this.style = style;
        this.avatarId = avatarId;
    }

    // --------------------------
    // Getters
    // --------------------------
    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getVoice() {
        return voice;
    }

    public double getStability() {
        return stability;
    }

    public double getStyle() {
        return style;
    }

    public String getAvatarId() {
        return avatarId;
    }
}