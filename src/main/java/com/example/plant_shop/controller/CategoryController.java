package com.example.plant_shop.controller;

import com.example.plant_shop.model.Category;
import com.example.plant_shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления категориями товаров.
 * <p>
 * Этот контроллер предоставляет функциональность для отображения списка категорий.
 * </p>
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Отображает список всех категорий товаров.
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для отображения списка категорий
     */
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.findAllMainCategories();
        model.addAttribute("categories", categories);
        return "categories";
    }
}
