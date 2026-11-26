package com.example.peliculas_frontend.actores;

import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

final class PaisesNacimiento {
    private static final Locale ESPANOL = Locale.forLanguageTag("es");

    static final List<String> NOMBRES = Arrays.stream(Locale.getISOCountries())
            .map(codigo -> new Locale.Builder().setRegion(codigo).build().getDisplayCountry(ESPANOL))
            .distinct()
            .sorted(Collator.getInstance(ESPANOL))
            .toList();

    private PaisesNacimiento() {
    }
}
