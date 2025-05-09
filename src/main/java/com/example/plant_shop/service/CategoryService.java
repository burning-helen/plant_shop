package com.example.plant_shop.service;

import com.example.plant_shop.model.Category;
import com.example.plant_shop.repository.CategoryRepository;
import com.example.plant_shop.repository.PlantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PlantRepository plantRepository;

    // Получаем все категории без родителя (главные категории)
    public List<Category> findAllMainCategories() {
        return categoryRepository.findByParentIsNull();
    }

    // Создание категории (с подкатегорией или без)
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Удаление категории
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }


    public List<Category> findSubcategories() {
        return categoryRepository.findAllByParentIsNotNull();
    }

    public List<Category> findSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<Category> findSubcategoriesByParentName(String parentName) {
        return categoryRepository.findByParent_Name(parentName);
    }

    @Transactional
    public void updateCategoryName(Long id, String newName) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        cat.setName(newName);
        // plants не надо менять руками — они хранят связь по FK, имя подтянется автоматически
    }

    @Transactional
    public void toggleActive(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        boolean now = !cat.isActive();
        cat.setActive(now);

        if (!now) {
            if (cat.getParent() == null) {

                plantRepository.deactivateByParentCategoryId(cat.getId());
                // …и все растения её подкатегорий:
                if (cat.getSubcategories() != null) {
                    cat.getSubcategories().forEach(sub ->
                            plantRepository.deactivateBySubcategoryId(sub.getId())
                    );
                }
            } else {
                // Это подкатегория → деактивируем только её растения
                plantRepository.deactivateBySubcategoryId(cat.getId());
            }
        }
        else {
            if (cat.getParent() == null) {

                plantRepository.activateByParentCategoryId(cat.getId());
                // …и все растения её подкатегорий:
                if (cat.getSubcategories() != null) {
                    cat.getSubcategories().forEach(sub ->
                            plantRepository.activateBySubcategoryId(sub.getId())
                    );
                }
            } else {
                // Это подкатегория → деактивируем только её растения
                plantRepository.activateBySubcategoryId(cat.getId());
            }
        }
        // если now==true (включаем) — не трогаем растения, они останутся неактивными
        // можно дописать логику «реактивации», если нужно
    }

    @Transactional
    public void updateName(Long id, String newName) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        cat.setName(newName);
        // Растения автоматически «увидят» новое имя
    }
}
