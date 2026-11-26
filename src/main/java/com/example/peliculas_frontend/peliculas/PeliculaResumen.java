package com.example.peliculas_frontend.peliculas;

import java.net.URI;

public record PeliculaResumen(long id, String titulo, int anio, int duracion,
                              String pais, String director, String genero, String portada) {

    public String urlPortada() {
        if (portada == null || portada.isBlank()) {
            return null;
        }

        try {
            URI url = URI.create(portada.strip());
            if (url.isAbsolute()) {
                String esquema = url.getScheme();
                return url.getHost() != null && ("https".equalsIgnoreCase(esquema)
                        || "http".equalsIgnoreCase(esquema)) ? url.toASCIIString() : null;
            }
            return url.getRawAuthority() == null && !portada.strip().startsWith("//")
                    && url.getPath() != null && !url.getPath().isEmpty() ? url.toASCIIString() : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
