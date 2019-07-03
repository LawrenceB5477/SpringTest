package com.lbeutlich.tacocloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lbeutlich.tacocloud.Ingredient.Type;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private IngredientRepository ingredientRepository;
    private TacoRepository tacoRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
    }

    @ModelAttribute(name="order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name="taco")
    public Taco taco() {
        return new Taco();
    }

    @ModelAttribute
    //called before request handler methods
    public void addIngredients(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();

        for (Ingredient ingredient : ingredientRepository.findAll()) {
            ingredients.add(ingredient);
        }

        for (Type type : Type.values()) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
    }

    @GetMapping
    public String showDesignForm(Model model) {
        model.addAttribute("design", new Taco());
        return "design" ;
    }

    @PostMapping
    //validation happens before method is called
    //when used as a method argument, indicates that value should be retrieved from the model
    public String processDesign(@Valid @ModelAttribute("design") Taco design, Errors errors, @ModelAttribute("order") Order order) {
        if (errors.hasErrors()) {
            log.error("Errors detected!");
            return "design";
        }


        log.info("Processing design: " + design);
        Taco saved = tacoRepository.save(design);
        order.addDesign(saved);
        log.info("Current session information: ");
        for (Taco taco : order.getTacos()) {
            log.info(taco.toString());
        }
        return "redirect:/orders/current";
    }

    private static List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        List<Ingredient>  res = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getType().equals(type)) {
                res.add(ingredient);
            }
        }
        return res;
    }
}
