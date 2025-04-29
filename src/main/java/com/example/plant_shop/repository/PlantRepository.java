package com.example.plant_shop.repository;

import com.example.plant_shop.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByParentCategory_Name(String categoryName);
    List<Plant> findBySubcategory_Name(String subcategoryName);

    @Query("SELECT p FROM Plant p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Plant> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("query") String query);

    List<Plant> findByPlantType(Plant.PlantType plantType);
}