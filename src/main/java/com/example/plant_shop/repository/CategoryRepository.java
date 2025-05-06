package com.example.plant_shop.repository;

import com.example.plant_shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Получаем все категории, у которых нет родителя (главные категории)
    List<Category> findByParentIsNull();
    List<Category> findAllByParentIsNotNull();

    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
    List<Category> findByParent_Name(String name);

}
