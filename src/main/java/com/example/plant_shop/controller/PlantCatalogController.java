package com.example.plant_shop.controller;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.PlantRepository;
import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.PlantService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PlantCatalogController {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    // Главная страница каталога
    @GetMapping("/catalog")
    public String showCatalog(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String subcategoryName,
            @RequestParam(required = false) String search,
            Model model, HttpSession session) {



        Cart cart = cartService.getCurrentCart(session, userService.getCurrentUser());
        model.addAttribute("cart", cart);

        List<Plant> plants;

        if (search != null && !search.isEmpty()) {
            plants = plantRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search);
            model.addAttribute("searchQuery", search);
        } else if (categoryName != null && !categoryName.isEmpty()) {
            if (subcategoryName != null && !subcategoryName.isEmpty()) {
                plants = plantRepository.findByParentCategory_NameAndSubcategory_Name(categoryName, subcategoryName);
                model.addAttribute("subcategoryName", subcategoryName);
            } else {
                plants = plantRepository.findByParentCategory_Name(categoryName);
            }
            model.addAttribute("categoryName", categoryName);
        } else {
            plants = plantRepository.findAll();
        }


        int count = cartService.getCartItemCount(session);
        model.addAttribute("cartCount", count);
        model.addAttribute("plants", plants);
        model.addAttribute("categories", categoryService.findAllMainCategories());
        model.addAttribute("subcategories", categoryService.findSubcategories());

        return "catalog";
    }

    // Просмотр растений по категории
    @GetMapping("/catalog/{category}/{subcategory}")
    public String showPlantsByCategoryAndSubcategory(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String subcategoryName,
            Model model) {

        List<Plant> plants;

        if (categoryName != null && subcategoryName != null) {
            // Поиск по обеим категориям
            plants = plantRepository.findByParentCategory_NameAndSubcategory_Name(categoryName, subcategoryName);
        } else if (categoryName != null) {
            // Поиск только по категории
            plants = plantRepository.findByParentCategory_Name(categoryName);
        } else if (subcategoryName != null) {
            // Поиск только по подкатегории
            plants = plantRepository.findBySubcategory_Name(subcategoryName);
        } else {
            // Если ни категория, ни подкатегория не заданы, показать все растения
            plants = plantRepository.findAll();
        }

        model.addAttribute("plants", plants);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("subcategoryName", subcategoryName);
        return "catalog";
    }


    // Поиск растений
    @GetMapping("/catalog/search")
    public String searchPlants(@RequestParam String query, Model model) {
        List<Plant> plants = plantRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query);        model.addAttribute("plants", plants);
        model.addAttribute("searchQuery", query);
        return "catalog";
    }

    // Детальная страница растения
    @GetMapping("/catalog/plant/{id}")
    public String showPlantDetails(@PathVariable Long id, Model model) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));
        model.addAttribute("plant", plant);
        return "plant_details";
    }
}