package com.example.plant_shop.controller;

import com.example.plant_shop.exception.NotFoundException;
import com.example.plant_shop.model.Category;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.repository.PlantRepository;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.PlantService;
import jakarta.transaction.Transactional;
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

/**
 * Контроллер для администрирования растений.
 * <p>
 * Этот контроллер обрабатывает запросы, связанные с управлением растениями в магазине, включая создание, редактирование,
 * изменение статуса активности и удаление растений. Доступ к этому контроллеру ограничен ролью администратора.
 * </p>
 */
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

    /**
     * Отображает список всех растений.
     * <p>
     * Этот метод загружает все растения и передает их в модель для отображения.
     * </p>
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для отображения списка растений
     */
    @GetMapping
    public String listPlants(Model model) {
        model.addAttribute("plants", plantService.findAllPlants());
        return "admin/products"; // Шаблон списка товаров
    }

    /**
     * Переключает статус активности растения.
     * <p>
     * Этот метод изменяет активность растения с данным id и перенаправляет на список растений.
     * </p>
     *
     * @param id идентификатор растения для изменения статуса
     * @param redirectAttributes атрибуты для перенаправления с сообщением об успехе
     * @return перенаправление на список растений
     */
    @PostMapping("/{id}/toggle-active")
    public String togglePlantActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        plantService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Статус товара изменен");
        return "redirect:/admin/products";
    }

    /**
     * Отображает форму для создания нового растения.
     * <p>
     * Этот метод загружает все родительские категории и подкатегории для отображения в форме создания.
     * </p>
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для создания нового растения
     */
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

    /**
     * Обрабатывает создание нового растения.
     * <p>
     * Этот метод сохраняет новое растение, включая его категорию и изображение.
     * </p>
     *
     * @param plant объект растения для создания
     * @param parentCategoryId идентификатор родительской категории
     * @param subCategoryId идентификатор подкатегории
     * @param imageFile файл изображения для растения
     * @return перенаправление на список растений
     * @throws IOException если произошла ошибка при сохранении изображения
     */
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

    /**
     * Отображает форму редактирования для существующего растения.
     * <p>
     * Этот метод загружает информацию о растении, родительских категориях и подкатегориях для редактирования.
     * </p>
     *
     * @param id идентификатор растения для редактирования
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для редактирования растения
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Plant plant = plantService.findPlantById(id);

        List<Category> categories = categoryService.findAllMainCategories();

        Map<Long, List<Category>> subcategoriesMap = new HashMap<>();
        for (Category category : categories) {
            subcategoriesMap.put(category.getId(),
                    categoryService.findSubcategoriesByParentId(category.getId()));
        }

        model.addAttribute("plant", plant);
        model.addAttribute("categories", categories);
        model.addAttribute("subcategoriesMap", subcategoriesMap);
        model.addAttribute("plantTypes", Plant.PlantType.values());

        if (plant.getParentCategory() != null) {
            model.addAttribute("subcategories",
                    subcategoriesMap.get(plant.getParentCategory().getId()));
        } else {
            model.addAttribute("subcategories", List.of());
        }

        return "admin/edit_product";
    }

    /**
     * Получает подкатегории для выбранной родительской категории.
     * <p>
     * Этот метод возвращает список подкатегорий для указанной родительской категории.
     * </p>
     *
     * @param parentId идентификатор родительской категории
     * @return список подкатегорий для указанной категории
     */
    @GetMapping("/categories/{parentId}/subcategories")
    @ResponseBody
    public List<Category> getSubcategories(@PathVariable Long parentId) {
        return categoryService.findSubcategoriesByParentId(parentId);
    }

    /**
     * Обрабатывает обновление информации о растении.
     * <p>
     * Этот метод обновляет данные растения, включая его категорию и изображение.
     * </p>
     *
     * @param id идентификатор растения для обновления
     * @param plant обновленные данные растения
     * @param parentCategoryId идентификатор родительской категории
     * @param subCategoryId идентификатор подкатегории
     * @param imageFile файл изображения для растения
     * @param redirectAttributes атрибуты для перенаправления с сообщением об успехе
     * @return перенаправление на список растений
     * @throws IOException если произошла ошибка при сохранении изображения
     */
    @PostMapping("/{id}/edit")
    public String updatePlant(
            @PathVariable Long id,
            @ModelAttribute Plant plant,
            @RequestParam("parentCategoryId") Long parentCategoryId,
            @RequestParam(value = "subCategoryId", required = false) Long subCategoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) throws IOException {

        if (plant.getName() == null || plant.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Название товара обязательно");
            return "redirect:/admin/products/" + id + "/edit";
        }

        Category parentCategory = categoryService.findById(parentCategoryId);
        plant.setParentCategory(parentCategory);

        if (subCategoryId != null) {
            Category subCategory = categoryService.findById(subCategoryId);

            plant.setSubcategory(subCategory);
        } else {
            plant.setSubcategory(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            plant.setImageUrl("/uploads/" + fileName);
        } else {
            Plant existingPlant = plantService.findPlantById(id);
            plant.setImageUrl(existingPlant.getImageUrl());
        }

        plant.setId(id);
        plantService.updatePlant(plant);

        redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен");
        return "redirect:/admin/products";
    }

    /**
     * Удаляет растение.
     * <p>
     * Этот метод деактивирует растение с данным id и перенаправляет на список растений.
     * </p>
     *
     * @param id идентификатор растения для удаления
     * @return перенаправление на список растений
     */
    @PostMapping("/{id}/delete")
    public String handleDeletePlant(@PathVariable Long id) {
        plantService.deactivatePlant(id);
        return "redirect:/admin/products";
    }

    /**
     * Сохраняет изображение для растения.
     * <p>
     * Этот метод сохраняет загруженный файл изображения на сервере и возвращает его имя.
     * </p>
     *
     * @param imageFile файл изображения
     * @return имя файла изображения
     * @throws IOException если произошла ошибка при сохранении файла
     */
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
