package org.learning.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.learning.springlamiapizzeriacrud.model.Pizza;
import org.learning.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {
    @Autowired
    private PizzaRepository pizzaRepository;

    @GetMapping
    public String index(Model model) {
        List<Pizza> pizzaList = pizzaRepository.findAll();
        model.addAttribute("pizzaList", pizzaList);
        return "pizzas/list";
    }

    @GetMapping("show/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isPresent()) {
            Pizza pizza = result.get();
            model.addAttribute("pizza", pizza);
            return "pizzas/show";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");
        }
    }

    //metodo che mostra pagina creazione di una pizza
    @GetMapping("/create")
    public String create(Model model) {
        //passo tramite model attributo di tipo pizza vuoto
        model.addAttribute("pizza", new Pizza());
        return "pizzas/create";
    }

    //metodo che riceve il submit del form di creazione e salva su dB la pizza
    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {
        //valido i dati del book cioè verifico se bindingResult ha errori
        if (bindingResult.hasErrors()) {
            return "pizzas/create";
        }
        //verifico se nome della pizza già è in DB

        //se esiste gia torna errore
        Optional<Pizza> pizzawithName = pizzaRepository.findByDescription(formPizza.getDescription());
        if (pizzawithName.isPresent()) {
            //se esiste gia torna errore
            bindingResult.addError(new FieldError("pizza", "description", formPizza.getDescription(), false, null, null,
                    "description must be unique"));
            return "pizzas/create";
        } else {
            //se sono validi li salvo su DB
            Pizza savedPizza = pizzaRepository.save(formPizza);

            //faccio redirect a pagina di dettaglio della pizza appena creata
            return "redirect:/pizzas/show/" + savedPizza.getId();

            //se non sono validi ricarico la pagina col form e i messaggi di errore

        }
    }
}
