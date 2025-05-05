package com.example.plant_shop.controller;

import com.example.plant_shop.exception.NotFoundException;
import com.example.plant_shop.model.Category;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.repository.PlantRepository;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminPlantController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private CategoryService categoryService;

    private static final String UPLOAD_DIR = "uploads/";
    @Autowired
    private PlantRepository plantRepository;

    @GetMapping
    public String listPlants(Model model) {
        model.addAttribute("plants", plantService.findAllPlants());
        return "admin/products"; // Шаблон списка товаров
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Category> parentCategories = categoryService.findAllMainCategories();
        Map<Long, List<Category>> subMap = new HashMap<>();

        for (Category parent : parentCategories) {
            subMap.put(parent.getId(), categoryService.findSubcategoriesByParentId(parent.getId()));
        }

        model.addAttribute("plant", new Plant());
        model.addAttribute("plantTypes", Arrays.asList(Plant.PlantType.values()));
        model.addAttribute("categories", parentCategories);
        model.addAttribute("subcategoriesMap", subMap);

        return "admin/create_product";
    }

    @PostMapping
    public String createPlant(@ModelAttribute Plant plant,
                              @RequestParam("parentCategoryId") Long parentCategoryId,
                              @RequestParam("subCategoryId") Long subCategoryId,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

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


        // Добавляем все необходимые атрибуты
        model.addAttribute("plant", plant);
        model.addAttribute("categories", categoryService.findAllMainCategories());
        model.addAttribute("plantTypes", Plant.PlantType.values());

        // Добавляем подкатегории для текущей категории растения
        if (plant.getParentCategory() != null) {
            model.addAttribute("subcategories",
                    categoryService.findSubcategoriesByParentId(plant.getParentCategory().getId()));
        } else {
            model.addAttribute("subcategories", List.of());
        }

        return "admin/edit_product";
    }

    @GetMapping("/subcategories")
    @ResponseBody
    public List<Category> getSubcategories(@RequestParam Long parentId) {
        return categoryService.findSubcategoriesByParentId(parentId);
    }

    @PostMapping("/{id}/edit")
    public String updatePlant(
            @PathVariable Long id,
            @ModelAttribute Plant plant,
            @RequestParam("parentCategoryId") Long parentCategoryId,
            @RequestParam(value = "subCategoryId", required = false) Long subCategoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) throws IOException {

        // Валидация
        if (plant.getName() == null || plant.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Название товара обязательно");
            return "redirect:/admin/products/" + id + "/edit";
        }

        // Обработка категорий
        Category parentCategory = categoryService.findById(parentCategoryId);
        plant.setParentCategory(parentCategory);

        if (subCategoryId != null) {
            Category subCategory = categoryService.findById(subCategoryId);

            plant.setSubcategory(subCategory);
        } else {
            plant.setSubcategory(null);
        }

        // Обработка изображения
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            plant.setImageUrl("/uploads/" + fileName);
        } else {
            // Сохраняем существующее изображение
            Plant existingPlant = plantService.findPlantById(id);
            plant.setImageUrl(existingPlant.getImageUrl());
        }

        plant.setId(id);
        plantService.updatePlant(plant);

        redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен");
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
