package com.aistream.service;

import com.aistream.config.PersonaConfig;
import com.aistream.model.Persona;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * PersonaService manages available AI personas.
 * Provides methods to retrieve, list, and validate personas.
 */
@Service
public class PersonaService {

    private final PersonaConfig personaConfig;

    public PersonaService(PersonaConfig personaConfig) {
        this.personaConfig = personaConfig;
    }

    /**
     * Returns the Persona enum by name (case-insensitive)
     * @param name persona name (e.g., "LIFE_COACH")
     * @return Optional<Persona>
     */
    public Optional<Persona> getPersonaByName(String name) {
        if (name == null) return Optional.empty();
        return Arrays.stream(Persona.values())
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Returns the default persona (used if none selected)
     */
    public Persona getDefaultPersona() {
        return Persona.valueOf(personaConfig.getDefault());
    }

    /**
     * Returns a list of all available personas
     */
    public List<Persona> getAllPersonas() {
        return Arrays.asList(Persona.values());
    }

    /**
     * Validates if a persona exists
     * @param name persona name
     * @return true if exists
     */
    public boolean isValidPersona(String name) {
        return getPersonaByName(name).isPresent();
    }

    /**
     * Get a persona by name or return default
     * @param name persona name
     * @return Persona object
     */
    public Persona getPersonaOrDefault(String name) {
        return getPersonaByName(name).orElse(getDefaultPersona());
    }
}