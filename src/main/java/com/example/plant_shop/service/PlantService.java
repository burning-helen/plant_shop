package com.example.plant_shop.service;

import com.example.plant_shop.model.Plant;
import com.example.plant_shop.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления растениями.
 * <p>
 * Этот сервис предоставляет методы для выполнения CRUD операций с растениями,
 * а также для активации и деактивации растений в системе.
 * </p>
 */
@Service
@Transactional
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    /**
     * Получает список всех растений.
     * <p>
     * Этот метод возвращает все растения в базе данных, включая активные и неактивные.
     * </p>
     *
     * @return список всех растений
     */
    public List<Plant> findAllPlants() {
        return plantRepository.findAll();
    }

    /**
     * Переключает статус активности растения по его идентификатору.
     * <p>
     * Этот метод переключает статус активности растения с активного на неактивное и наоборот.
     * </p>
     *
     * @param id идентификатор растения
     * @throws RuntimeException если растение с данным идентификатором не найдено
     */
    @Transactional
    public void toggleActive(Long id) {
        Plant plant = plantRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));

        if (plant.isActive()) {
            plant.setActive(false);
        } else {
            plant.setActive(true);
        }
        plantRepository.save(plant);
    }

    /**
     * Находит растение по идентификатору.
     * <p>
     * Этот метод возвращает растение по его идентификатору, включая неактивные растения.
     * </p>
     *
     * @param id идентификатор растения
     * @return найденное растение
     * @throws RuntimeException если растение с данным идентификатором не найдено
     */
    public Plant findPlantById(Long id) {
        return plantRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));
    }

    /**
     * Создает новое растение.
     * <p>
     * Этот метод сохраняет новое растение в базе данных.
     * </p>
     *
     * @param plant объект растения, который необходимо сохранить
     */
    public void createPlant(Plant plant) {
        plantRepository.save(plant);
    }

    /**
     * Обновляет информацию о растении.
     * <p>
     * Этот метод сохраняет изменения, внесенные в существующее растение.
     * </p>
     *
     * @param plant объект растения с обновленными данными
     */
    public void updatePlant(Plant plant) {
        plantRepository.save(plant);
    }

    /**
     * Деактивирует растение по его идентификатору.
     * <p>
     * Этот метод устанавливает статус растения как неактивный.
     * </p>
     *
     * @param id идентификатор растения, которое нужно деактивировать
     * @throws RuntimeException если растение с данным идентификатором не найдено
     */
    @Transactional
    public void deactivatePlant(Long id) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + id));

        plant.setActive(false);
        plantRepository.save(plant);
    }
}
