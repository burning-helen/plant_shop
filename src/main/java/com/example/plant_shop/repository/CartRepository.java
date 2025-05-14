package com.example.plant_shop.repository;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с объектами {@link Cart}.
 * <p>
 * Этот интерфейс предоставляет методы для взаимодействия с сущностями {@link Cart} в базе данных.
 * Он включает поиск корзины пользователя по его данным.
 * </p>
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Находит корзину по пользователю.
     *
     * @param user пользователь, чью корзину нужно найти.
     * @return опциональный объект, содержащий корзину пользователя, если она существует.
     */
    Optional<Cart> findByUser(User user);
}
