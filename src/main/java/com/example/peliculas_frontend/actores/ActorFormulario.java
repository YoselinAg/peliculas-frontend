package com.example.peliculas_frontend.actores;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ActorFormulario(
        @NotBlank(message = "Escribe el nombre.")
        @Size(max = 150, message = "El nombre admite hasta 150 caracteres.")
        String nombre,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull(message = "Indica la fecha de nacimiento.")
        @PastOrPresent(message = "La fecha de nacimiento no puede estar en el futuro.")
        LocalDate fechaNacimiento,

        @NotBlank(message = "Escribe el país de nacimiento.")
        @Size(max = 100, message = "El país admite hasta 100 caracteres.")
        String paisNacimiento) {

    static ActorFormulario desde(ActorFicha actor) {
        return new ActorFormulario(actor.nombre(), actor.fechaNacimiento(), actor.paisNacimiento());
    }
}
