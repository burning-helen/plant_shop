package com.example.plant_shop.repository;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.CartItem;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для работы с объектами {@link CartItem}.
 * <p>
 * Этот интерфейс предоставляет методы для взаимодействия с сущностями {@link CartItem} в базе данных.
 * Включает поиск товаров в корзине пользователя, а также удаление товаров из корзины.
 * </p>
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Находит все товары в корзине пользователя.
     *
     * @param user пользователь, чьи товары в корзине нужно найти.
     * @return список товаров в корзине данного пользователя.
     */
    List<CartItem> findByUser(User user);

    /**
     * Находит товары по их идентификаторам и пользователю.
     *
     * @param itemIds идентификаторы товаров.
     * @param user пользователь, которому принадлежат эти товары.
     * @return список товаров с указанными идентификаторами для данного пользователя.
     */
    List<CartItem> findByIdInAndUser(List<Long> itemIds, User user);

    /**
     * Находит товары в корзине конкретного пользователя по их идентификаторам.
     *
     * @param user пользователь, чьи товары нужно найти.
     * @param selectedItemIds идентификаторы выбранных товаров.
     * @return список товаров в корзине указанного пользователя.
     */
    List<CartItem> findByUserAndIdIn(User user, List<Long> selectedItemIds);

    /**
     * Удаляет все товары из корзины.
     *
     * @param cart корзина, товары из которой нужно удалить.
     */
    void deleteAllByCart(Cart cart);
}
