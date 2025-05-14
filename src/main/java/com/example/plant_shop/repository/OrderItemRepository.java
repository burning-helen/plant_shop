package com.example.plant_shop.repository;

import com.example.plant_shop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с объектами {@link OrderItem}.
 * <p>
 * Этот интерфейс предоставляет методы для взаимодействия с сущностями {@link OrderItem} в базе данных.
 * Он наследует стандартные операции CRUD из {@link JpaRepository}, такие как сохранение, удаление и поиск по идентификатору.
 * </p>
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
