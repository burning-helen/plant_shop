package com.example.plant_shop.repository;

import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с объектами {@link User}.
 * <p>
 * Этот интерфейс предоставляет методы для выполнения стандартных операций CRUD с сущностями {@link User}.
 * Он также включает метод для поиска пользователя по имени пользователя.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return {@link Optional} с найденным пользователем, если он существует
     */
    Optional<User> findByUsername(String username);
}
