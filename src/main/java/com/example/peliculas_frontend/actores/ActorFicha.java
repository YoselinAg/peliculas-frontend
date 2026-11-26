package com.example.peliculas_frontend.actores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ActorFicha(long id, String nombre, LocalDate fechaNacimiento, String paisNacimiento) {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern(
            "d MMM uuuu", Locale.forLanguageTag("es-ES"));

    public String fechaVisible() {
        return fechaNacimiento.format(FECHA);
    }

    public String iniciales() {
        return Stream.of(nombre.strip().split("\\s+"))
                .limit(2)
                .map(palabra -> palabra.substring(0, palabra.offsetByCodePoints(0, 1)))
                .collect(Collectors.joining())
                .toUpperCase(Locale.ROOT);
    }
}
