package com.example.plant_shop.config;

import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.CategoryService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        return cartService.getCartItemCount(session);
    }

    @ModelAttribute("categories")
    public Object categories() {
        return categoryService.findAllMainCategories();
    }

    @ModelAttribute("subcategories")
    public Object subcategories() {
        return categoryService.findSubcategories();
    }

    @ModelAttribute("user")
    public Object user() {
        return userService.getCurrentUser();
    }


}
