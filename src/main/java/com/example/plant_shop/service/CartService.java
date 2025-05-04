package com.example.plant_shop.service;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.CartItem;
import com.example.plant_shop.model.Plant;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.CartItemRepository;
import com.example.plant_shop.repository.CartRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Cart getCurrentCart(HttpSession session, User user) {
        if (user != null) {

            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        newCart.setItems(new ArrayList<>());
                        newCart.setTotalAmount(0.0);
                        return cartRepository.save(newCart);
                    });

            // Синхронизируем корзину из сессии, если она есть
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
                mergeSessionCartIntoDbCart(sessionCart, cart);
                session.removeAttribute("cart"); // Удаляем корзину из сессии
                cartRepository.save(cart);
            }

            return cart;
        } else {
            // Для неавторизованного пользователя используем сессию
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                cart.setItems(new ArrayList<>());
                cart.setTotalAmount(0.0);
                session.setAttribute("cart", cart);
            }
            return cart;
        }
    }

    @Transactional
    public void mergeSessionCartIntoDbCart(Cart sessionCart, Cart dbCart) {
        for (CartItem sessionItem : sessionCart.getItems()) {
            Optional<CartItem> existingItem = dbCart.getItems().stream()
                    .filter(item -> item.getPlant().getId() == sessionItem.getPlant().getId())
                    .findFirst();

            if (existingItem.isPresent()) {
                CartItem dbItem = existingItem.get();
                dbItem.setQuantity(dbItem.getQuantity() + sessionItem.getQuantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setPlant(sessionItem.getPlant());
                newItem.setQuantity(sessionItem.getQuantity());
                newItem.setPrice(sessionItem.getPrice());
                newItem.setCart(dbCart);
                dbCart.getItems().add(newItem);
            }
        }

        // Пересчитываем общую сумму
        double total = dbCart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        dbCart.setTotalAmount(total);
    }

    @Transactional
    public void addToCart(Plant plant, int quantity, HttpSession session, User user) {
        Cart cart = getCurrentCart(session, user);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getPlant().getId() == plant.getId())
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setPlant(plant);
            newItem.setQuantity(quantity);
            newItem.setPrice(plant.getPrice());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(total);

        if (user != null) {
            cartRepository.save(cart);
        } else {
            session.setAttribute("cart", cart);
        }
    }

    @Transactional
    public int getCartItemCount(HttpSession session) {
        Cart cart = getCurrentCart(session, userService.getCurrentUser());
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void saveCart(Cart cart, HttpSession session, User user) {
        cart.setUser(user);

        for (CartItem item : cart.getItems()) {
            item.setCart(cart);
        }

        cartRepository.save(cart);
        session.setAttribute("cart", cart);
    }


    @Transactional
    public void updateItemQuantity(Long itemId, int newQuantity, HttpSession session, User user) {
        Cart cart = getCurrentCart(session, user);

        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(itemId)) {
                if (newQuantity <= 0) {
                    cart.getItems().remove(item);
                    cartItemRepository.delete(item); // Явное удаление
                } else {
                    // Убедимся, что новое количество не превышает stock
                    int maxAllowed = item.getPlant().getStockQuantity();
                    item.setQuantity(Math.min(newQuantity, maxAllowed));
                }
                break;
            }
        }

        // Пересчет общей суммы
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalAmount(total);

        if (user != null) {
            cartRepository.save(cart);
        } else {
            session.setAttribute("cart", cart);
        }
    }

    @Transactional
    public void removeItem(Long itemId, HttpSession session, User user) {
        Cart cart = getCurrentCart(session, user);

        Iterator<CartItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getId().equals(itemId)) {
                iterator.remove();
                cartItemRepository.delete(item);
                break;
            }
        }


        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(total);

        if (user != null) {
            cartRepository.save(cart);
        } else {
            session.setAttribute("cart", cart);
        }
    }




}
