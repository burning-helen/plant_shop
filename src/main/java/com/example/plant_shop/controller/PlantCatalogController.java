package com.example.plant_shop.controller;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.Category;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.CategoryRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private CategoryRepository categoryRepository;

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
        } else if (categoryName != null && !categoryName.isEmpty() && subcategoryName != null && !subcategoryName.isEmpty()) {
            plants = plantRepository.findActiveByParentAndSubcategory(categoryName, subcategoryName);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("subcategoryName", subcategoryName);
        } else if (categoryName != null && !categoryName.isEmpty()) {
            plants = plantRepository.findActiveByParentCategory_Name(categoryName);
            model.addAttribute("categoryName", categoryName);
        } else if (subcategoryName != null && !subcategoryName.isEmpty()) {
            plants = plantRepository.findActiveBySubcategory_Name(subcategoryName);
            model.addAttribute("subcategoryName", subcategoryName);
        } else {
            plants = plantRepository.findAllActive();
        }

        int count = cartService.getCartItemCount(session);
        model.addAttribute("cartCount", count);
        model.addAttribute("plants", plants);
        model.addAttribute("categories", categoryService.findAllMainCategories());

        // Загружаем подкатегории только для выбранной категории
        List<Category> subcategories = categoryName != null ?
                categoryService.findSubcategoriesByParentName(categoryName) :
                Collections.emptyList();
        model.addAttribute("subcategories", subcategories);

        return "catalog";
    }

    @GetMapping("/catalog/subcategories")
    @ResponseBody
    public List<Category> getSubcategories(@RequestParam String parent) {
        return categoryService.findSubcategoriesByParentName(parent);
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
            plants = plantRepository.findActiveByParentAndSubcategory(categoryName, subcategoryName);
        } else if (categoryName != null) {
            // Поиск только по категории
            plants = plantRepository.findActiveByParentCategory_Name(categoryName);
        } else if (subcategoryName != null) {
            // Поиск только по подкатегории
            plants = plantRepository.findActiveBySubcategory_Name(subcategoryName);
        } else {
            // Если ни категория, ни подкатегория не заданы, показать все растения
            plants = plantRepository.findAllActive();
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