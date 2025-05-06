package com.example.plant_shop.service;

import com.example.plant_shop.model.Category;
import com.example.plant_shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

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
}
