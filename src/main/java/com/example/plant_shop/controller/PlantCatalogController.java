package com.example.plant_shop.controller;

import com.example.plant_shop.model.Plant;
import com.example.plant_shop.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Главная страница каталога
    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        List<Plant> allPlants = plantRepository.findAll();
        model.addAttribute("plants", allPlants);
        return "catalog";
    }

    // Просмотр растений по категории
    @GetMapping("/catalog/category/{categoryName}")
    public String showPlantsByCategory(@PathVariable String categoryName, Model model) {
        List<Plant> plants = plantRepository.findByParentCategory_Name(categoryName);
        model.addAttribute("plants", plants);
        model.addAttribute("categoryName", categoryName);
        return "catalog";
    }

    // Просмотр растений по подкатегории
    @GetMapping("/catalog/subcategory/{subcategoryName}")
    public String showPlantsBySubcategory(@PathVariable String subcategoryName, Model model) {
        List<Plant> plants = plantRepository.findBySubcategory_Name(subcategoryName);
        model.addAttribute("plants", plants);
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