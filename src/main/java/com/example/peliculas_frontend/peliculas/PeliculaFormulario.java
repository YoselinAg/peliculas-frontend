package com.example.peliculas_frontend.peliculas;

import com.example.peliculas_frontend.actores.ActorFicha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PeliculaFormulario(
        @NotBlank(message = "Escribe el título.")
        @Size(max = 200, message = "El título admite hasta 200 caracteres.") String titulo,
        @NotNull(message = "Indica el año.")
        @Positive(message = "El año debe ser mayor que cero.") Integer anio,
        @NotNull(message = "Indica la duración.")
        @Positive(message = "La duración debe ser mayor que cero.") Integer duracion,
        @NotBlank(message = "Escribe el país.")
        @Size(max = 100, message = "El país admite hasta 100 caracteres.") String pais,
        @NotBlank(message = "Indica la dirección.")
        @Size(max = 150, message = "La dirección admite hasta 150 caracteres.") String director,
        @NotBlank(message = "Escribe el género.")
        @Size(max = 100, message = "El género admite hasta 100 caracteres.") String genero,
        String sinopsis,
        @Size(max = 500, message = "La portada admite hasta 500 caracteres.") String portada,
        List<@NotNull @Positive Long> actorIds) {

    public PeliculaFormulario {
        if (actorIds == null) {
            actorIds = List.of();
        }
    }

    static PeliculaFormulario desde(PeliculaResumen pelicula) {
        return new PeliculaFormulario(pelicula.titulo(), pelicula.anio(), pelicula.duracion(), pelicula.pais(),
                pelicula.director(), pelicula.genero(), pelicula.sinopsis(), pelicula.portada(),
                pelicula.actores().stream().map(ActorFicha::id).toList());
    }
}
