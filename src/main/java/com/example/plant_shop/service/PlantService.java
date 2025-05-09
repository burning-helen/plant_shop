package com.example.plant_shop.service;

import com.example.plant_shop.model.Plant;
import com.example.plant_shop.model.Plant.PlantType;
import com.example.plant_shop.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    // Основные CRUD операции
    public List<Plant> findAllPlants() {
        return plantRepository.findAll();
    }

    @Transactional
    public void toggleActive(Long id) {
        Plant plant = plantRepository.findByIdIncludingInactive(id).
                orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));;
        if (plant.isActive()) {
            plant.setActive(false);
        }
        else {
            plant.setActive(true);
        }
        plantRepository.save(plant);
    }


    public Plant findPlantById(Long id) {
        return plantRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));
    }

    public void createPlant(Plant plant) {
        plantRepository.save(plant);
    }

    public void updatePlant(Plant plant) {
        plantRepository.save(plant);
    }
    @Transactional
    public void deactivatePlant(Long id) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));
        plant.setActive(false);
        plantRepository.save(plant);
    }


    // Методы для каталога
    public List<Plant> findAllAvailablePlants() {
        return plantRepository.findByStockQuantityGreaterThan(0);
    }

    public List<Plant> findByCategory(String categoryName) {
        return plantRepository.findActiveByParentCategory_Name(categoryName);
    }

    public List<Plant> findBySubcategory(String subcategoryName) {
        return plantRepository.findActiveBySubcategory_Name(subcategoryName);
    }

    public List<Plant> findByPlantType(PlantType plantType) {
        return plantRepository.findByPlantType(plantType);
    }

    public List<Plant> searchPlants(String query) {
        return plantRepository.searchPlants(query.toLowerCase());
    }

    // Методы для работы с количеством на складе
    @Transactional
    public void decreaseStockQuantity(Long plantId, int quantity) {
        Plant plant = findPlantById(plantId);
        if (plant.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        plant.setStockQuantity(plant.getStockQuantity() - quantity);
        plantRepository.save(plant);
    }

    @Transactional
    public void increaseStockQuantity(Long plantId, int quantity) {
        Plant plant = findPlantById(plantId);
        plant.setStockQuantity(plant.getStockQuantity() + quantity);
        plantRepository.save(plant);
    }

    // Методы для проверки доступности
    public boolean isAvailable(Long plantId, int quantity) {
        return plantRepository.findById(plantId)
                .map(plant -> plant.getStockQuantity() >= quantity)
                .orElse(false);
    }
}