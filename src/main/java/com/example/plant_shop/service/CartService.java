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

import java.math.BigDecimal;
import java.util.*;

/**
 * Сервис для работы с корзиной покупок.
 * <p>
 * Этот сервис управляет корзинами пользователей, позволяя добавлять, обновлять, удалять товары, а также сохранять данные в сессии или в базе данных для авторизованных пользователей.
 * </p>
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    /**
     * Получает текущую корзину пользователя.
     * <p>
     * Для авторизованных пользователей корзина хранится в базе данных, а для неавторизованных - в сессии.
     * Если корзина не существует, она создается.
     * </p>
     *
     * @param session текущая HTTP-сессия
     * @param user    авторизованный пользователь
     * @return текущая корзина
     */
    @Transactional
    public Cart getCurrentCart(HttpSession session, User user) {
        if (user != null) {
            // Для авторизованного пользователя
            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        newCart.setItems(new ArrayList<>());
                        newCart.setTotalAmount(0.0);
                        return cartRepository.save(newCart);
                    });

            // Синхронизируем корзину из сессии
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
                mergeSessionCartIntoDbCart(sessionCart, cart);
                session.removeAttribute("cart"); // Удаляем корзину из сессии
                cartRepository.save(cart);
            }

            return cart;
        } else {
            // Для неавторизованного пользователя
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

    /**
     * Объединяет корзину из сессии с корзиной в базе данных для авторизованного пользователя.
     * <p>
     * Если в корзине из сессии есть товары, которых нет в базе данных, они добавляются в корзину пользователя.
     * </p>
     *
     * @param sessionCart корзина из сессии
     * @param dbCart     корзина пользователя в базе данных
     */
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
                newItem.setUser(userService.getCurrentUser());
                dbCart.getItems().add(newItem);
            }
        }

        // Пересчитываем общую сумму
        double total = dbCart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        dbCart.setTotalAmount(total);
    }

    /**
     * Добавляет товар в корзину пользователя.
     * <p>
     * Если товар уже существует в корзине, увеличивается его количество, иначе добавляется новый товар.
     * </p>
     *
     * @param plant   добавляемый товар
     * @param quantity количество товара
     * @param session текущая HTTP-сессия
     * @param user    авторизованный пользователь
     */
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
            newItem.setUser(userService.getCurrentUser());
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

    /**
     * Получает общее количество товаров в корзине пользователя.
     *
     * @param session текущая HTTP-сессия
     * @return общее количество товаров в корзине
     */
    @Transactional
    public int getCartItemCount(HttpSession session) {
        Cart cart = getCurrentCart(session, userService.getCurrentUser());
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Обновляет количество товара в корзине сессии.
     *
     * @param plantId     ID товара
     * @param newQuantity новое количество
     * @param session     текущая HTTP-сессия
     */
    @Transactional
    public void updateSessionCartItem(Long plantId, int newQuantity, HttpSession session) {
        Cart cart = getCurrentCart(session, null);

        // Находим элемент корзины по plantId
        for (CartItem item : cart.getItems()) {
            if (item.getPlant().getId().equals(plantId)) {
                item.setQuantity(newQuantity);
                break;
            }
        }

        // Пересчет общей суммы
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalAmount(total);

        // Обновляем корзину в сессии
        session.setAttribute("cart", cart);
    }

    /**
     * Обновляет количество товара в корзине пользователя и пересчитывает общую сумму.
     * Если количество становится меньше или равно нулю, товар удаляется из корзины.
     *
     * @param itemId      ID товара в корзине
     * @param newQuantity новое количество
     * @param session     текущая HTTP-сессия
     * @param user        авторизованный пользователь
     */
    @Transactional
    public void updateItemQuantity(Long itemId, int newQuantity, HttpSession session, User user) {
        Cart cart = getCurrentCart(session, user);

        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            if (newQuantity <= 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                int maxAllowed = item.getPlant().getStockQuantity();
                item.setQuantity(Math.min(newQuantity, maxAllowed));
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

    /**
     * Удаляет товар из корзины пользователя.
     *
     * @param itemId ID товара, который нужно удалить
     * @param session текущая HTTP-сессия
     * @param user    авторизованный пользователь
     */
    @Transactional
    public void removeItem(Long itemId, HttpSession session, User user) {
        Cart cart = getCurrentCart(session, user);

        // Проверяем существование товара
        boolean removed = cart.getItems().removeIf(item -> {
            if (item.getId().equals(itemId)) {
                return true;
            }
            return false;
        });

        if (!removed) {
            throw new IllegalArgumentException("Товар с ID " + itemId + " не найден в корзине");
        }

        // Обновляем общую сумму
        double newTotal = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(newTotal);

        // Сохраняем изменения
        cartRepository.save(cart);
    }

    /**
     * Рассчитывает общую сумму для выбранных товаров.
     *
     * @param itemIds список ID выбранных товаров
     * @return общая сумма для выбранных товаров
     */
    public BigDecimal calculateTotal(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        List<CartItem> items = getItemsByIds(itemIds);

        for (CartItem item : items) {
            if (item == null) {
                continue;
            }

            // Используем цену товара
            double price = item.getPrice();
            int quantity = item.getQuantity() > 0 ? item.getQuantity() : 0;

            total = total.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)));
        }

        return total;
    }

    /**
     * Получает список товаров по их ID.
     *
     * @param selectedItemIds список ID товаров
     * @return список товаров
     */
    public List<CartItem> getItemsByIds(List<Long> selectedItemIds) {
        return cartItemRepository.findAllById(selectedItemIds);
    }
}
