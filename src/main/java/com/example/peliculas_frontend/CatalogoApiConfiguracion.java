package com.example.peliculas_frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
class CatalogoApiConfiguracion {
    @Bean
    RestClient catalogoApi(@Value("${catalogo.api.url}") String url) {
        var conexiones = new SimpleClientHttpRequestFactory();
        conexiones.setConnectTimeout(Duration.ofSeconds(3));
        conexiones.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder().baseUrl(url).requestFactory(conexiones).build();
    }
}
