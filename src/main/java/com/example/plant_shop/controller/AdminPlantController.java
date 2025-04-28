package com.example.plant_shop.controller;

import com.example.plant_shop.model.Category;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminPlantController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private CategoryService categoryService;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public String listPlants(Model model) {
        model.addAttribute("plants", plantService.findAllPlants());
        return "admin/products"; // Шаблон списка товаров
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Category> parentCategories = categoryService.findAllMainCategories();

        model.addAttribute("plant", new Plant());
        model.addAttribute("plantTypes", Arrays.asList(Plant.PlantType.values()));
        model.addAttribute("categories", parentCategories);
        // Убрали общий список подкатегорий, будем загружать их динамически
        return "admin/create_product";
    }

    @PostMapping
    public String createPlant(@ModelAttribute Plant plant,
                              @RequestParam("parentCategoryId") Long parentCategoryId,
                              @RequestParam("subCategoryId") Long subCategoryId,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        // Устанавливаем категории
        Category parentCategory = categoryService.findById(parentCategoryId);
        Category subCategory = categoryService.findById(subCategoryId);

        plant.setParentCategory(parentCategory);
        plant.setSubcategory(subCategory);

        if (!imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            plant.setImageUrl("/" + UPLOAD_DIR + fileName);
        }

        plantService.createPlant(plant);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Plant plant = plantService.findPlantById(id);
        List<Category> parentCategories = categoryService.findAllMainCategories();

        if (plant != null) {
            model.addAttribute("plant", plant);
            model.addAttribute("categories", parentCategories);
            model.addAttribute("subcategories", categoryService.findSubcategories());
            model.addAttribute("plantTypes", Arrays.asList(Plant.PlantType.values()));
            return "admin/edit_product";
        } else {
            return "redirect:/admin/products";
        }
    }

    @GetMapping("/subcategories")
    @ResponseBody
    public List<Category> getSubcategories(@RequestParam Long parentId) {
        return categoryService.findSubcategoriesByParentId(parentId);
    }

    @PostMapping("/{id}/edit")
    public String updatePlant(@PathVariable Long id,
                              @ModelAttribute Plant plant,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        plant.setId(id);

        if (!imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            plant.setImageUrl("/" + UPLOAD_DIR + fileName);
        } else {
            // Если новое изображение не загружено, оставить старое
            Plant existingPlant = plantService.findPlantById(id);
            if (existingPlant != null) {
                plant.setImageUrl(existingPlant.getImageUrl());
            }
        }

        plantService.updatePlant(plant);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return "redirect:/admin/products";
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        File uploadDirFile = new File(UPLOAD_DIR);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, imageFile.getBytes());

        return fileName;
    }
}
