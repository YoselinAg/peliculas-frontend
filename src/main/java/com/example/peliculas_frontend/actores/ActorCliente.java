package com.example.peliculas_frontend.actores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
class ActorCliente {
    private static final String ACTORES = "/api/actores";
    private static final String FICHA = ACTORES + "/{id}";

    private final RestClient api;

    ActorCliente(@Value("${catalogo.api.url}") String url) {
        var conexiones = new SimpleClientHttpRequestFactory();
        conexiones.setConnectTimeout(Duration.ofSeconds(3));
        conexiones.setReadTimeout(Duration.ofSeconds(5));
        api = RestClient.builder().baseUrl(url).requestFactory(conexiones).build();
    }

    List<ActorFicha> listar(String nombre, String paisNacimiento) {
        return api.get()
                .uri(uri -> uri.path(ACTORES)
                        .queryParam("nombre", "{nombre}")
                        .queryParam("paisNacimiento", "{pais}")
                        .build(Map.of("nombre", nombre, "pais", paisNacimiento)))
                .retrieve()
                .requiredBody(new ParameterizedTypeReference<List<ActorFicha>>() {});
    }

    ActorFicha consultar(long id) {
        return api.get().uri(FICHA, id).retrieve().requiredBody(ActorFicha.class);
    }

    void crear(ActorFormulario datos) {
        api.post().uri(ACTORES).contentType(MediaType.APPLICATION_JSON)
                .body(datos).retrieve().toBodilessEntity();
    }

    void actualizar(long id, ActorFormulario datos) {
        api.put().uri(FICHA, id).contentType(MediaType.APPLICATION_JSON)
                .body(datos).retrieve().toBodilessEntity();
    }

    void eliminar(long id) {
        api.delete().uri(FICHA, id).retrieve().toBodilessEntity();
    }
}
