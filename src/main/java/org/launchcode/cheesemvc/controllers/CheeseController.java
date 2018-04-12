package org.launchcode.cheesemvc.controllers;


import org.launchcode.cheesemvc.models.Category;
import org.launchcode.cheesemvc.models.Cheese;
import org.launchcode.cheesemvc.models.Menu;
import org.launchcode.cheesemvc.models.data.CategoryDao;
import org.launchcode.cheesemvc.models.data.CheeseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId,
                                       Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/add";
        }

        Category cat = categoryDao.findById(categoryId).orElse(null);
        newCheese.setCategory(cat);
        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");

        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm( @RequestParam int[] cheeseIds ) {

        for (int cheeseId : cheeseIds) {
            cheeseDao.deleteById(cheeseId);
        }

        return "redirect:";
    }

    @RequestMapping(value = "category", method = RequestMethod.GET)
    public String category(Model model, @RequestParam int id) {

        Category cat = categoryDao.findById(id).orElse(null);
        List<Cheese> cheeses = cat.getCheeses();
        model.addAttribute("cheeses", cheeses);
        model.addAttribute("title", "Cheeses in Category" + cat.getName());
        return "cheese/index";

    }

    @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {
        Cheese cheese = cheeseDao.findById(cheeseId).orElse(null);
        model.addAttribute("name", cheese.getName());
        model.addAttribute("description", cheese.getDescription());
        model.addAttribute("cheeseId", cheese.getId());
        model.addAttribute("categories", categoryDao.findAll());

        return "cheese/edit";
    }

    @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.POST)
    public String processEditForm(@PathVariable int cheeseId, @RequestParam String name,
                                  @RequestParam String description, @RequestParam int categoryId ) {
        Cheese cheese = cheeseDao.findById(cheeseId).orElse(null);
        Category cat = categoryDao.findById(categoryId).orElse(null);
        cheese.setName(name);
        cheese.setDescription(description);
        cheese.setCategory(cat);
        cheeseDao.save(cheese);
        return "redirect:/cheese";
    }

}
