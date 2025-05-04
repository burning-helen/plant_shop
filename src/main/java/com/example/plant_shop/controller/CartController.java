package com.example.plant_shop.controller;

import com.example.plant_shop.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.PlantRepository;
import com.example.plant_shop.model.CartItem;

import com.example.plant_shop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Cart cart = cartService.getCurrentCart(session, userService.getCurrentUser() );
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Long plantId,
                                         @RequestParam(defaultValue = "1") int quantity,
                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Plant plant = plantRepository.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

            cartService.addToCart(plant, quantity, session, userService.getCurrentUser());
            response.put("success", true);
            response.put("message", "Товар добавлен в корзину");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при добавлении товара: " + e.getMessage());
        }
        return response;
    }


    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getCartItemCount(HttpSession session) {
        Cart cart = cartService.getCurrentCart(session, userService.getCurrentUser());
        int count = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        System.out.println(count);
        return String.valueOf(count);
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long itemId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        cartService.updateItemQuantity(itemId, quantity, session, userService.getCurrentUser());
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long itemId,
                                 HttpSession session) {
        cartService.removeItem(itemId, session, userService.getCurrentUser());

        return "redirect:/cart";
    }



}
