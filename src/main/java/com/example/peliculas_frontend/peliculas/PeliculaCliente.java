package com.example.peliculas_frontend.peliculas;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class PeliculaCliente {
    private static final String PELICULAS = "/api/peliculas";
    private static final String FICHA = PELICULAS + "/{id}";
    private final RestClient api;

    PeliculaCliente(RestClient api) {
        this.api = api;
    }

    List<PeliculaResumen> listar(String titulo, String genero, String actor) {
        return api.get()
                .uri(uri -> uri.path(PELICULAS)
                        .queryParam("titulo", "{titulo}")
                        .queryParam("genero", "{genero}")
                        .queryParam("actor", "{actor}")
                        .build(Map.of("titulo", titulo, "genero", genero, "actor", actor)))
                .retrieve()
                .requiredBody(new ParameterizedTypeReference<List<PeliculaResumen>>() {});
    }

    PeliculaResumen consultar(long id) {
        return api.get().uri(FICHA, id).retrieve().requiredBody(PeliculaResumen.class);
    }

    void crear(PeliculaFormulario datos) {
        api.post().uri(PELICULAS).contentType(MediaType.APPLICATION_JSON)
                .body(datos).retrieve().toBodilessEntity();
    }

    void actualizar(long id, PeliculaFormulario datos) {
        api.put().uri(FICHA, id).contentType(MediaType.APPLICATION_JSON)
                .body(datos).retrieve().toBodilessEntity();
    }

    void eliminar(long id) {
        api.delete().uri(FICHA, id).retrieve().toBodilessEntity();
    }
}
