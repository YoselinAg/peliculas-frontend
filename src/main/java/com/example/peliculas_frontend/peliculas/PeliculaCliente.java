package com.example.peliculas_frontend.peliculas;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class PeliculaCliente {
    private final RestClient api;

    PeliculaCliente(RestClient api) {
        this.api = api;
    }

    List<PeliculaResumen> listar(String titulo, String genero, String actor) {
        return api.get()
                .uri(uri -> uri.path("/api/peliculas")
                        .queryParam("titulo", "{titulo}")
                        .queryParam("genero", "{genero}")
                        .queryParam("actor", "{actor}")
                        .build(Map.of("titulo", titulo, "genero", genero, "actor", actor)))
                .retrieve()
                .requiredBody(new ParameterizedTypeReference<List<PeliculaResumen>>() {});
    }
}
