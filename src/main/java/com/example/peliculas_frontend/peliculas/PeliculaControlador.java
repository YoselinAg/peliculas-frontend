package com.example.peliculas_frontend.peliculas;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
class PeliculaControlador {
    private static final int PELICULAS_POR_PAGINA = 10;

    private final PeliculaCliente cliente;

    PeliculaControlador(PeliculaCliente cliente) {
        this.cliente = cliente;
    }

    @GetMapping("/peliculas")
    String listar(@RequestParam(name = "titulo", defaultValue = "") String titulo,
                  @RequestParam(name = "genero", defaultValue = "") String genero,
                  @RequestParam(name = "actor", defaultValue = "") String actor,
                  @RequestParam(name = "pagina", defaultValue = "1") int pagina,
                  Model model) {
        titulo = titulo.strip();
        genero = genero.strip();
        actor = actor.strip();
        List<PeliculaResumen> peliculas = cliente.listar(titulo, genero, actor);
        int total = peliculas.size();
        int paginas = total == 0 ? 1 : (total - 1) / PELICULAS_POR_PAGINA + 1;
        int actual = Math.max(1, Math.min(pagina, paginas));
        int desde = (actual - 1) * PELICULAS_POR_PAGINA;
        int hasta = desde + Math.min(PELICULAS_POR_PAGINA, total - desde);

        model.addAttribute("titulo", titulo);
        model.addAttribute("genero", genero);
        model.addAttribute("actor", actor);
        model.addAttribute("filtrado", !titulo.isEmpty() || !genero.isEmpty() || !actor.isEmpty());
        model.addAttribute("peliculas", peliculas.subList(desde, hasta));
        model.addAttribute("totalPeliculas", total);
        model.addAttribute("pagina", actual);
        model.addAttribute("paginas", paginas);
        return "peliculas/listado";
    }

    @ExceptionHandler(RestClientException.class)
    ModelAndView catalogoNoDisponible() {
        return new ModelAndView("peliculas/error", HttpStatus.BAD_GATEWAY);
    }
}
