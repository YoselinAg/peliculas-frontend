package com.example.peliculas_frontend;

import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Paises {
    private static final Locale ESPANOL = Locale.forLanguageTag("es");

    public static final List<String> NOMBRES = Arrays.stream(Locale.getISOCountries())
            .map(codigo -> new Locale.Builder().setRegion(codigo).build().getDisplayCountry(ESPANOL))
            .distinct()
            .sorted(Collator.getInstance(ESPANOL))
            .toList();

    private Paises() {
    }
}
