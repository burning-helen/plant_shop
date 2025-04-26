package com.example.plant_shop.repository;

import com.example.plant_shop.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByCategory(String category);
}
