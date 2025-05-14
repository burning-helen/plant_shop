package com.example.plant_shop.repository;

import com.example.plant_shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с объектами {@link Category}.
 * <p>
 * Этот интерфейс предоставляет методы для взаимодействия с сущностями {@link Category} в базе данных.
 * Он включает методы для получения категорий, как главных, так и подкатегорий.
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Получает все категории, которые не имеют родительской категории (главные категории).
     *
     * @return список главных категорий.
     */
    List<Category> findByParentIsNull();

    /**
     * Получает все категории, у которых есть родительская категория (подкатегории).
     *
     * @return список подкатегорий.
     */
    List<Category> findAllByParentIsNotNull();

    /**
     * Получает все категории, которые имеют заданный родительский идентификатор.
     *
     * @param parentId идентификатор родительской категории.
     * @return список категорий, которые принадлежат указанному родителю.
     */
    List<Category> findByParentId(Long parentId);

    /**
     * Получает все категории, которые имеют родительскую категорию с заданным именем.
     *
     * @param name имя родительской категории.
     * @return список категорий, чьи родительские категории имеют указанное имя.
     */
    List<Category> findByParent_Name(String name);
}
