package com.example.plant_shop.config;

import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Класс для добавления общих атрибутов модели в представления.
 * <p>
 * Этот класс используется для добавления общих данных в модель, которые будут доступны во всех контроллерах.
 * С помощью аннотации {@link ModelAttribute} атрибуты автоматически добавляются в модель и становятся доступными
 * во всех представлениях.
 * </p>
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Метод для получения количества товаров в корзине для текущего пользователя.
     *
     * @param session HTTP-сессия, используемая для получения информации о корзине пользователя.
     * @return количество товаров в корзине.
     */
    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        return cartService.getCartItemCount(session);
    }

    /**
     * Метод для получения всех основных категорий товаров.
     *
     * @return список основных категорий товаров.
     */
    @ModelAttribute("categories")
    public Object categories() {
        return categoryService.findAllMainCategories();
    }

    /**
     * Метод для получения всех подкатегорий товаров.
     *
     * @return список подкатегорий товаров.
     */
    @ModelAttribute("subcategories")
    public Object subcategories() {
        return categoryService.findSubcategories();
    }

    /**
     * Метод для получения текущего пользователя.
     *
     * @return объект текущего пользователя, если он аутентифицирован.
     */
    @ModelAttribute("user")
    public Object user() {
        return userService.getCurrentUser();
    }
}
