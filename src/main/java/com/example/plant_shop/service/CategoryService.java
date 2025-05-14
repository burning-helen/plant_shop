package com.example.plant_shop.service;

import com.example.plant_shop.model.Category;
import com.example.plant_shop.repository.CategoryRepository;
import com.example.plant_shop.repository.PlantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с категориями растений.
 * <p>
 * Этот сервис управляет категориями и подкатегориями, позволяя создавать, удалять и обновлять категории, а также изменять их статус активности.
 * </p>
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PlantRepository plantRepository;

    /**
     * Получает все главные категории, которые не имеют родителя.
     * <p>
     * Главные категории - это категории без родительских элементов.
     * </p>
     *
     * @return список главных категорий
     */
    public List<Category> findAllMainCategories() {
        return categoryRepository.findByParentIsNull();
    }

    /**
     * Создает новую категорию.
     * <p>
     * Новая категория может быть как главной, так и подкатегорией, в зависимости от предоставленных данных.
     * </p>
     *
     * @param category категория для создания
     * @return созданная категория
     */
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Удаляет категорию по ее идентификатору.
     * <p>
     * Если категория имеет подкатегории или растения, они также могут быть затронуты.
     * </p>
     *
     * @param id идентификатор категории
     */
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Получает все подкатегории, то есть категории, которые имеют родителя.
     *
     * @return список подкатегорий
     */
    public List<Category> findSubcategories() {
        return categoryRepository.findAllByParentIsNotNull();
    }

    /**
     * Получает подкатегории для указанного родителя по его идентификатору.
     *
     * @param parentId идентификатор родительской категории
     * @return список подкатегорий для указанного родителя
     */
    public List<Category> findSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    /**
     * Получает категорию по её идентификатору.
     *
     * @param id идентификатор категории
     * @return категория с указанным идентификатором
     * @throws RuntimeException если категория не найдена
     */
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    /**
     * Получает подкатегории для категории с указанным именем родителя.
     *
     * @param parentName имя родительской категории
     * @return список подкатегорий для указанного родителя
     */
    public List<Category> findSubcategoriesByParentName(String parentName) {
        return categoryRepository.findByParent_Name(parentName);
    }

    /**
     * Переключает активность категории.
     * <p>
     * Если категория была активна, она становится неактивной, и все связанные с ней растения также становятся неактивными.
     * Если категория была неактивной, она становится активной, и растения снова становятся активными.
     * </p>
     *
     * @param id идентификатор категории
     */
    @Transactional
    public void toggleActive(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));

        boolean now = !cat.isActive();
        cat.setActive(now);

        if (!now) {
            if (cat.getParent() == null) {
                plantRepository.deactivateByParentCategoryId(cat.getId());
                // Деактивируем растения всех подкатегорий:
                if (cat.getSubcategories() != null) {
                    cat.getSubcategories().forEach(sub ->
                            plantRepository.deactivateBySubcategoryId(sub.getId())
                    );
                }
            } else {
                // Это подкатегория → деактивируем только её растения
                plantRepository.deactivateBySubcategoryId(cat.getId());
            }
        } else {
            if (cat.getParent() == null) {
                plantRepository.activateByParentCategoryId(cat.getId());
                // Активируем растения всех подкатегорий:
                if (cat.getSubcategories() != null) {
                    cat.getSubcategories().forEach(sub ->
                            plantRepository.activateBySubcategoryId(sub.getId())
                    );
                }
            } else {
                // Это подкатегория → активируем только её растения
                plantRepository.activateBySubcategoryId(cat.getId());
            }
        }
    }

    /**
     * Обновляет название категории.
     *
     * @param id       идентификатор категории
     * @param newName новое название категории
     * @throws EntityNotFoundException если категория не найдена
     */
    @Transactional
    public void updateName(Long id, String newName) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        cat.setName(newName);
    }
}
