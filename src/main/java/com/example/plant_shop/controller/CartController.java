package com.example.plant_shop.controller;

import com.example.plant_shop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.security.Principal;
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
        return String.valueOf(count);
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestParam String itemId, // Может быть "plant_123" или обычный ID
            @RequestParam int quantity,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = userService.getCurrentUser();

            if (itemId.startsWith("plant_")) {
                // Обработка для неавторизованных пользователей
                Long plantId = Long.parseLong(itemId.substring(6));
                cartService.updateSessionCartItem(plantId, quantity, session);
            } else {
                // Обработка для авторизованных пользователей
                Long id = Long.parseLong(itemId);
                cartService.updateItemQuantity(id, quantity, session, currentUser);
            }

            Cart cart = cartService.getCurrentCart(session, currentUser);
            response.put("success", true);
            response.put("totalAmount", cart.getTotalAmount());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeItem(@RequestParam Long itemId, HttpSession session, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("success", false);
            response.put("redirect", "/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // здесь логика удаления
        cartService.removeItem(itemId, session, userService.getCurrentUser());
        response.put("success", true);
        return ResponseEntity.ok(response);
    }



}