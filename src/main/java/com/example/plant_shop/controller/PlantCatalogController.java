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

/**
 * Контроллер для отображения каталога растений и управления фильтрацией, поиском и просмотром деталей растений.
 * <p>
 * Этот контроллер обрабатывает запросы для отображения списка растений, фильтрации по категориям и подкатегориям,
 * а также поиска по названию и описанию растений. Также предоставляется возможность просмотра подробной информации о растении.
 * </p>
 */
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

    /**
     * Отображает каталог растений с возможностью фильтрации по категориям, подкатегориям и поиску.
     * <p>
     * Пользователь может выбрать категорию, подкатегорию или выполнить поиск по названию или описанию растения.
     * Также отображается информация о количестве товаров в корзине.
     * </p>
     *
     * @param categoryName название категории для фильтрации
     * @param subcategoryName название подкатегории для фильтрации
     * @param search строка для поиска растений по имени или описанию
     * @param model модель для передачи данных на страницу
     * @param session текущая HTTP сессия
     * @return имя шаблона для отображения каталога растений
     */
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

        List<Category> subcategories = categoryName != null ?
                categoryService.findSubcategoriesByParentName(categoryName) :
                Collections.emptyList();
        model.addAttribute("subcategories", subcategories);

        return "catalog";
    }

    /**
     * Возвращает список подкатегорий для выбранной родительской категории.
     * <p>
     * Этот метод используется для динамической загрузки подкатегорий при выборе категории.
     * </p>
     *
     * @param parent название родительской категории
     * @return список подкатегорий для выбранной категории
     */
    @GetMapping("/catalog/subcategories")
    @ResponseBody
    public List<Category> getSubcategories(@RequestParam String parent) {
        return categoryService.findSubcategoriesByParentName(parent);
    }

    /**
     * Отображает список растений для выбранной категории и подкатегории.
     * <p>
     * Этот метод используется для фильтрации растений по категории и подкатегории.
     * </p>
     *
     * @param categoryName название категории
     * @param subcategoryName название подкатегории
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для отображения каталога с отфильтрованными растениями
     */
    @GetMapping("/catalog/{category}/{subcategory}")
    public String showPlantsByCategoryAndSubcategory(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String subcategoryName,
            Model model) {

        List<Plant> plants;

        if (categoryName != null && subcategoryName != null) {
            plants = plantRepository.findActiveByParentAndSubcategory(categoryName, subcategoryName);
        } else if (categoryName != null) {
            plants = plantRepository.findActiveByParentCategory_Name(categoryName);
        } else if (subcategoryName != null) {
            plants = plantRepository.findActiveBySubcategory_Name(subcategoryName);
        } else {
            plants = plantRepository.findAllActive();
        }

        model.addAttribute("plants", plants);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("subcategoryName", subcategoryName);
        return "catalog";
    }

    /**
     * Осуществляет поиск растений по запросу.
     * <p>
     * Этот метод позволяет пользователю искать растения по названию или описанию.
     * </p>
     *
     * @param query строка запроса для поиска
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для отображения каталога с результатами поиска
     */
    @GetMapping("/catalog/search")
    public String searchPlants(@RequestParam String query, Model model) {
        List<Plant> plants = plantRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query);
        model.addAttribute("plants", plants);
        model.addAttribute("searchQuery", query);
        return "catalog";
    }

    /**
     * Отображает подробную информацию о растении.
     * <p>
     * Этот метод отображает страницу с подробной информацией о выбранном растении.
     * </p>
     *
     * @param id идентификатор растения
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для отображения подробностей о растении
     */
    @GetMapping("/catalog/plant/{id}")
    public String showPlantDetails(@PathVariable Long id, Model model) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));
        model.addAttribute("plant", plant);
        return "plant_details";
    }
}
