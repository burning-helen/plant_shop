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

/**
 * Контроллер для администрирования категорий растений.
 * <p>
 * Этот контроллер обрабатывает запросы, связанные с управлением категориями растений,
 * включая создание, редактирование и активацию/деактивацию категорий. Доступ к этому контроллеру
 * ограничен ролью администратора.
 * </p>
 */
@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Отображает список всех категорий.
     * <p>
     * Этот метод загружает все главные категории (категории без родителя) и передает их в модель для отображения.
     * </p>
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для отображения списка категорий
     */
    @GetMapping
    public String listCategories(Model model) {
        // Получаем все главные категории (без родителя)
        List<Category> categories = categoryService.findAllMainCategories();
        model.addAttribute("categories", categories);
        return "admin/categories"; // Шаблон для отображения списка категорий
    }

    /**
     * Отображает форму для создания новой категории.
     * <p>
     * Этот метод загружает все главные категории для отображения в выпадающем списке родительских категорий.
     * Также передается список доступных типов растений.
     * </p>
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для создания категории
     */
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

    /**
     * Обрабатывает создание новой категории.
     * <p>
     * Этот метод сохраняет новую категорию, отправленную из формы, и перенаправляет на список категорий.
     * </p>
     *
     * @param category объект категории для создания
     * @return перенаправление на список категорий
     */
    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories"; // Перенаправление на список категорий
    }

    /**
     * Обрабатывает редактирование названия категории.
     * <p>
     * Этот метод обновляет название категории с данным id и перенаправляет на список категорий.
     * </p>
     *
     * @param id идентификатор категории для редактирования
     * @param name новое название категории
     * @return перенаправление на список категорий
     */
    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id,
                               @RequestParam String name) {
        categoryService.updateName(id, name);
        return "redirect:/admin/categories";
    }

    /**
     * Изменяет статус активности категории (активна/неактивна).
     * <p>
     * Этот метод переключает активность категории с данным id и перенаправляет на список категорий.
     * </p>
     *
     * @param id идентификатор категории для изменения активности
     * @return перенаправление на список категорий
     */
    @PostMapping("/toggle/{id}")
    public String toggleCategory(@PathVariable Long id) {
        categoryService.toggleActive(id);
        return "redirect:/admin/categories";
    }
}
