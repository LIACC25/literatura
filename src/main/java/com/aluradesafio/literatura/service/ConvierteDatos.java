package com.aluradesafio.literatura.service;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos {

    private final ObjectMapper objectMapper;

    public ConvierteDatos() {
        this.objectMapper = new ObjectMapper();
    }

    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir JSON a objeto: " + e.getMessage(), e);
        }
    }
}
