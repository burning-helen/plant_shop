package com.example.plant_shop.service;

import com.example.plant_shop.model.Plant;
import com.example.plant_shop.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    public List<Plant> findAllPlants() {
        return plantRepository.findAll();
    }

    public Plant findPlantById(Long id) {
        return plantRepository.findById(id).orElse(null);
    }

    public void createPlant(Plant plant) {
        plantRepository.save(plant);
    }

    public void updatePlant(Plant plant) {
        plantRepository.save(plant);
    }

    public void deletePlant(Long id) {
        plantRepository.deleteById(id);
    }


}

