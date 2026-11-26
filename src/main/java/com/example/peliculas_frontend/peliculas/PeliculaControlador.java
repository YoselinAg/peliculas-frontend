package com.example.peliculas_frontend.peliculas;

import com.example.peliculas_frontend.Paises;
import com.example.peliculas_frontend.actores.ActorCliente;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
class PeliculaControlador {
    private static final int PELICULAS_POR_PAGINA = 10;
    private static final String FORMULARIO = "peliculas/formulario";
    private static final String LISTADO = "redirect:/peliculas";
    private static final List<String> GENEROS = List.of(
            "Acción", "Animación", "Aventura", "Bélico", "Biográfico", "Ciencia ficción",
            "Comedia", "Crimen", "Documental", "Drama", "Familiar", "Fantasía",
            "Histórico", "Misterio", "Musical", "Romance", "Suspense", "Terror", "Western");

    private final PeliculaCliente cliente;
    private final ActorCliente actores;

    PeliculaControlador(PeliculaCliente cliente, ActorCliente actores) {
        this.cliente = cliente;
        this.actores = actores;
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

    @GetMapping("/peliculas/{id}")
    String ficha(@PathVariable("id") long id, Model model) {
        model.addAttribute("pelicula", cliente.consultar(id));
        return "peliculas/ficha";
    }

    @GetMapping("/peliculas/nueva")
    String nueva(Model model) {
        model.addAttribute("peliculaFormulario",
                new PeliculaFormulario("", null, null, "", "", "", "", "", List.of()));
        return formulario(model);
    }

    @GetMapping("/peliculas/{id}/editar")
    String editar(@PathVariable("id") long id, Model model) {
        PeliculaResumen pelicula = cliente.consultar(id);
        model.addAttribute("peliculaId", id);
        model.addAttribute("peliculaFormulario", PeliculaFormulario.desde(pelicula));
        if (!Paises.NOMBRES.contains(pelicula.pais())) {
            model.addAttribute("paisAnterior", pelicula.pais());
        }
        if (!GENEROS.contains(pelicula.genero())) {
            model.addAttribute("generoAnterior", pelicula.genero());
        }
        return formulario(model);
    }

    @PostMapping("/peliculas")
    String crear(@Valid @ModelAttribute("peliculaFormulario") PeliculaFormulario datos,
                 BindingResult validacion, Model model, RedirectAttributes redirect) {
        return guardar(null, datos, validacion, model, redirect);
    }

    @PostMapping("/peliculas/{id}")
    String actualizar(@PathVariable("id") long id,
                      @Valid @ModelAttribute("peliculaFormulario") PeliculaFormulario datos,
                      BindingResult validacion, Model model, RedirectAttributes redirect) {
        model.addAttribute("peliculaId", id);
        return guardar(id, datos, validacion, model, redirect);
    }

    private String guardar(Long id, PeliculaFormulario datos, BindingResult validacion,
                           Model model, RedirectAttributes redirect) {
        if (validacion.hasErrors()) {
            return formulario(model);
        }
        if (!Paises.NOMBRES.contains(datos.pais())) {
            validacion.rejectValue("pais", "pais.invalido", "Selecciona un país de la lista.");
        }
        if (!GENEROS.contains(datos.genero())) {
            validacion.rejectValue("genero", "genero.invalido", "Selecciona un género de la lista.");
        }
        if (validacion.hasErrors()) {
            return formulario(model);
        }

        try {
            if (id == null) {
                cliente.crear(datos);
                redirect.addFlashAttribute("aviso", "Película añadida al catálogo.");
            } else {
                cliente.actualizar(id, datos);
                redirect.addFlashAttribute("aviso", "Ficha actualizada.");
            }
            return LISTADO;
        } catch (HttpClientErrorException.BadRequest error) {
            model.addAttribute("error", "El catálogo rechazó los datos. Revisa los campos y el reparto.");
            return formulario(model);
        }
    }

    private String formulario(Model model) {
        model.addAttribute("actores", actores.listar("", ""));
        model.addAttribute("paises", Paises.NOMBRES);
        model.addAttribute("generos", GENEROS);
        return FORMULARIO;
    }

    @GetMapping("/peliculas/{id}/eliminar")
    String confirmarBaja(@PathVariable("id") long id, Model model) {
        model.addAttribute("pelicula", cliente.consultar(id));
        return "peliculas/eliminar";
    }

    @PostMapping("/peliculas/{id}/eliminar")
    String eliminar(@PathVariable("id") long id, RedirectAttributes redirect) {
        cliente.eliminar(id);
        redirect.addFlashAttribute("aviso", "Película eliminada del catálogo.");
        return LISTADO;
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ModelAndView fichaAusente() {
        return error(HttpStatus.NOT_FOUND, "Ficha no disponible", "Puede que la película ya se haya eliminado.");
    }

    @ExceptionHandler(RestClientException.class)
    ModelAndView catalogoNoDisponible() {
        return error(HttpStatus.BAD_GATEWAY, "Catálogo no disponible",
                "Vuelve al listado para comprobar el estado de la ficha.");
    }

    private ModelAndView error(HttpStatus estado, String titulo, String mensaje) {
        ModelAndView vista = new ModelAndView("peliculas/error", estado);
        vista.addObject("titulo", titulo);
        vista.addObject("mensaje", mensaje);
        return vista;
    }
}
