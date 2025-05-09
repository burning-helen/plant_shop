package com.example.plant_shop.repository;

import com.example.plant_shop.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    @Query("SELECT p FROM Plant p WHERE p.active = true AND p.parentCategory.name = :categoryName")
    List<Plant> findActiveByParentCategory_Name(String categoryName);

    @Query("SELECT p FROM Plant p WHERE p.active = true AND p.subcategory.name = :subcategoryName")
    List<Plant> findActiveBySubcategory_Name(String subcategoryName);


    @Query("SELECT p FROM Plant p WHERE p.active = true AND(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Plant> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("query") String query);

    List<Plant> findByPlantType(Plant.PlantType plantType);

    List<Plant> findByStockQuantityGreaterThan(int quantity);

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

    List<Plant> findAll();

    @Query("SELECT p FROM Plant p WHERE p.active = true")
    List<Plant> findAllActive();
    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = false
        WHERE p.parentCategory.id = :catId
    """)
    void deactivateByParentCategoryId(@Param("catId") Long parentCategoryId);

    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = false
        WHERE p.subcategory.id = :subcatId
    """)
    void deactivateBySubcategoryId(@Param("subcatId") Long subcategoryId);


    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = true
        WHERE p.parentCategory.id = :catId
    """)
    void activateByParentCategoryId(@Param("catId") Long parentCategoryId);

    @Modifying
    @Query("""
        UPDATE Plant p
        SET p.active = true
        WHERE p.subcategory.id = :subcatId
    """)
    void activateBySubcategoryId(@Param("subcatId") Long subcategoryId);



    @Query(value = "SELECT * FROM plants WHERE id = :id", nativeQuery = true)
    Optional<Plant> findByIdIncludingInactive(@Param("id") Long id);

    @Query("SELECT p FROM Plant p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Plant> searchPlants(@Param("query") String query);

    @Query("SELECT p FROM Plant p WHERE p.stockQuantity >= :quantity")
    List<Plant> findAvailablePlantsWithMinimumQuantity(@Param("quantity") int quantity);
}