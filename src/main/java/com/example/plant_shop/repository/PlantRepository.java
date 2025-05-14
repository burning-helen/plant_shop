package com.example.plant_shop.repository;

import com.example.plant_shop.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с объектами {@link Plant}.
 * <p>
 * Этот интерфейс предоставляет методы для выполнения различных операций с сущностями {@link Plant} в базе данных.
 * Он наследует стандартные операции CRUD из {@link JpaRepository}, а также включает дополнительные запросы для фильтрации, активации/деактивации растений и поиска по различным критериям.
 * </p>
 */
public interface PlantRepository extends JpaRepository<Plant, Long> {

    /**
     * Находит активные растения по имени родительской категории.
     *
     * @param categoryName имя родительской категории
     * @return список активных растений, принадлежащих категории
     */
    @Query("SELECT p FROM Plant p WHERE p.active = true AND p.parentCategory.name = :categoryName")
    List<Plant> findActiveByParentCategory_Name(String categoryName);

    /**
     * Находит активные растения по имени подкатегории.
     *
     * @param subcategoryName имя подкатегории
     * @return список активных растений, принадлежащих подкатегории
     */
    @Query("SELECT p FROM Plant p WHERE p.active = true AND p.subcategory.name = :subcategoryName")
    List<Plant> findActiveBySubcategory_Name(String subcategoryName);

    /**
     * Находит растения, чьи название или описание содержат указанный текст, игнорируя регистр.
     *
     * @param query строка для поиска
     * @return список растений, соответствующих запросу
     */
    @Query("SELECT p FROM Plant p WHERE p.active = true AND(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Plant> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("query") String query);

    /**
     * Находит растения по типу растения.
     *
     * @param plantType тип растения
     * @return список растений, соответствующих типу
     */
    List<Plant> findByPlantType(Plant.PlantType plantType);

    /**
     * Находит растения, у которых количество в наличии больше указанного значения.
     *
     * @param quantity минимальное количество
     * @return список растений с количеством больше указанного
     */
    List<Plant> findByStockQuantityGreaterThan(int quantity);

    /**
     * Находит активные растения, принадлежащие указанным родительской категории и подкатегории.
     *
     * @param parentCategoryName имя родительской категории
     * @param subcategoryName имя подкатегории
     * @return список активных растений, соответствующих категории и подкатегории
     */
    @Query("""
        SELECT p FROM Plant p 
        WHERE p.active = true 
        AND p.parentCategory.name = :parentCategoryName 
        AND p.subcategory.name = :subcategoryName
    """)
    List<Plant> findActiveByParentAndSubcategory(
            @Param("parentCategoryName") String parentCategoryName,
            @Param("subcategoryName") String subcategoryName
    );

    /**
     * Находит все растения.
     *
     * @return список всех растений
     */
    List<Plant> findAll();

    /**
     * Находит все активные растения.
     *
     * @return список всех активных растений
     */
    @Query("SELECT p FROM Plant p WHERE p.active = true")
    List<Plant> findAllActive();

    /**
     * Деактивирует все растения, принадлежащие указанной родительской категории.
     *
     * @param parentCategoryId идентификатор родительской категории
     */
    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = false
        WHERE p.parentCategory.id = :catId
    """)
    void deactivateByParentCategoryId(@Param("catId") Long parentCategoryId);

    /**
     * Деактивирует все растения, принадлежащие указанной подкатегории.
     *
     * @param subcategoryId идентификатор подкатегории
     */
    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = false
        WHERE p.subcategory.id = :subcatId
    """)
    void deactivateBySubcategoryId(@Param("subcatId") Long subcategoryId);

    /**
     * Активирует все растения, принадлежащие указанной родительской категории.
     *
     * @param parentCategoryId идентификатор родительской категории
     */
    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = true
        WHERE p.parentCategory.id = :catId
    """)
    void activateByParentCategoryId(@Param("catId") Long parentCategoryId);

    /**
     * Активирует все растения, принадлежащие указанной подкатегории.
     *
     * @param subcategoryId идентификатор подкатегории
     */
    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = true
        WHERE p.subcategory.id = :subcatId
    """)
    void activateBySubcategoryId(@Param("subcatId") Long subcategoryId);

    /**
     * Находит растение по его идентификатору, включая неактивные растения.
     *
     * @param id идентификатор растения
     * @return растение с указанным идентификатором, если оно существует
     */
    @Query(value = "SELECT * FROM plants WHERE id = :id", nativeQuery = true)
    Optional<Plant> findByIdIncludingInactive(@Param("id") Long id);

    /**
     * Ищет растения по названию или описанию, используя нечувствительный к регистру поиск.
     *
     * @param query строка для поиска
     * @return список растений, соответствующих запросу
     */
    @Query("SELECT p FROM Plant p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Plant> searchPlants(@Param("query") String query);
}
