package com.example.plant_shop.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Модель категории растения.
 * Категория может быть родительской или подкатегорией, а также связана с растениями.
 */
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Уникальный идентификатор категории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название категории.
     */
    private String name;

    /**
     * Родительская категория (если есть).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Список подкатегорий текущей категории.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories;

    /**
     * Растения, относящиеся к этой категории как к родительской.
     */
    @OneToMany(mappedBy = "parentCategory")
    private List<Plant> plantsAsParentCategory;

    /**
     * Флаг активности категории.
     * Неактивные категории могут быть скрыты от пользователей.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Растения, относящиеся к этой категории как к подкатегории.
     */
    @OneToMany(mappedBy = "subcategory")
    private List<Plant> plantsAsSubcategory;

    /**
     * Конструктор по умолчанию.
     */
    public Category() {}

    /**
     * Конструктор с указанием имени категории.
     * @param name название категории
     */
    public Category(String name) {
        this.name = name;
    }

    // --- Геттеры и сеттеры ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public List<Category> getSubcategories() { return subcategories; }
    public void setSubcategories(List<Category> subcategories) { this.subcategories = subcategories; }

    public List<Plant> getPlantsAsParentCategory() { return plantsAsParentCategory; }
    public void setPlantsAsParentCategory(List<Plant> plantsAsParentCategory) { this.plantsAsParentCategory = plantsAsParentCategory; }

    public List<Plant> getPlantsAsSubcategory() { return plantsAsSubcategory; }
    public void setPlantsAsSubcategory(List<Plant> plantsAsSubcategory) { this.plantsAsSubcategory = plantsAsSubcategory; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
