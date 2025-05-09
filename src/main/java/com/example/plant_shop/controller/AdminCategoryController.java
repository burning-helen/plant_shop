package com.example.plant_shop.controller;

import com.example.plant_shop.model.Plant;
import org.springframework.ui.Model;
import com.example.plant_shop.model.Category;
import com.example.plant_shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        // Получаем все главные категории (без родителя)
        List<Category> categories = categoryService.findAllMainCategories();
        model.addAttribute("categories", categories);
        return "admin/categories"; // Шаблон для отображения списка категорий
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Получаем все главные категории для выпадающего списка
        List<Category> parentCategories = categoryService.findAllMainCategories();
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("subcategories", categoryService.findSubcategories()); // список подкатегорий
        model.addAttribute("plantTypes", List.of(Plant.PlantType.values()));// Передаем родительские категории для создания подкатегории
        return "admin/create_category"; // Шаблон для создания категории
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories"; // Перенаправление на список категорий
    }



    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id,
                               @RequestParam String name) {
        categoryService.updateName(id, name);
        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCategory(@PathVariable Long id) {
        categoryService.toggleActive(id);
        return "redirect:/admin/categories";
    }
}
