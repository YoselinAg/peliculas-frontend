package com.example.peliculas_frontend.actores;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class ActorControlador {
    private static final String LISTADO = "redirect:/actores";
    private static final String FORMULARIO = "actores/formulario";

    private final ActorCliente cliente;

    ActorControlador(ActorCliente cliente) {
        this.cliente = cliente;
    }

    @GetMapping("/")
    String inicio() {
        return LISTADO;
    }

    @GetMapping("/actores")
    String listar(@RequestParam(name = "nombre", defaultValue = "") String nombre,
                  @RequestParam(name = "paisNacimiento", defaultValue = "") String paisNacimiento,
                  Model model) {
        nombre = nombre.strip();
        paisNacimiento = paisNacimiento.strip();
        model.addAttribute("nombre", nombre);
        model.addAttribute("paisNacimiento", paisNacimiento);
        model.addAttribute("filtrado", !nombre.isEmpty() || !paisNacimiento.isEmpty());
        model.addAttribute("actores", cliente.listar(nombre, paisNacimiento));
        return "actores/listado";
    }

    @GetMapping("/actores/nuevo")
    String nuevo(Model model) {
        model.addAttribute("actorFormulario", new ActorFormulario("", null, ""));
        return FORMULARIO;
    }

    @GetMapping("/actores/{id}/editar")
    String editar(@PathVariable("id") long id, Model model) {
        model.addAttribute("actorId", id);
        model.addAttribute("actorFormulario", ActorFormulario.desde(cliente.consultar(id)));
        return FORMULARIO;
    }

    @PostMapping("/actores")
    String crear(@Valid @ModelAttribute("actorFormulario") ActorFormulario datos,
                 BindingResult validacion, Model model, RedirectAttributes redirect) {
        return guardar(null, datos, validacion, model, redirect);
    }

    @PostMapping("/actores/{id}")
    String actualizar(@PathVariable("id") long id,
                      @Valid @ModelAttribute("actorFormulario") ActorFormulario datos,
                      BindingResult validacion, Model model, RedirectAttributes redirect) {
        model.addAttribute("actorId", id);
        return guardar(id, datos, validacion, model, redirect);
    }

    private String guardar(Long id, ActorFormulario datos, BindingResult validacion,
                           Model model, RedirectAttributes redirect) {
        if (validacion.hasErrors()) {
            return FORMULARIO;
        }

        try {
            if (id == null) {
                cliente.crear(datos);
                redirect.addFlashAttribute("aviso", "Actor añadido al catálogo.");
            } else {
                cliente.actualizar(id, datos);
                redirect.addFlashAttribute("aviso", "Ficha actualizada.");
            }
            return LISTADO;
        } catch (HttpClientErrorException.BadRequest error) {
            model.addAttribute("error", "El catálogo rechazó los datos. Revisa la ficha y vuelve a guardarla.");
            return FORMULARIO;
        }
    }

    @GetMapping("/actores/{id}/eliminar")
    String confirmarBaja(@PathVariable("id") long id, Model model) {
        model.addAttribute("actor", cliente.consultar(id));
        return "actores/eliminar";
    }

    @PostMapping("/actores/{id}/eliminar")
    String eliminar(@PathVariable("id") long id, RedirectAttributes redirect) {
        cliente.eliminar(id);
        redirect.addFlashAttribute("aviso", "Ficha eliminada del catálogo.");
        return LISTADO;
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ModelAndView fichaAusente() {
        return error(HttpStatus.NOT_FOUND, "Ficha no disponible", "Puede que el actor ya se haya eliminado.");
    }

    @ExceptionHandler(RestClientException.class)
    ModelAndView catalogoNoDisponible() {
        return error(HttpStatus.BAD_GATEWAY, "No se pudo completar la operación",
                "Comprueba que el servicio del catálogo esté disponible. Si acababas de guardar, revisa primero el listado.");
    }

    private ModelAndView error(HttpStatus estado, String titulo, String mensaje) {
        ModelAndView vista = new ModelAndView("actores/error", estado);
        vista.addObject("titulo", titulo);
        vista.addObject("mensaje", mensaje);
        return vista;
    }
}
