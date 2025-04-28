package com.example.plant_shop.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent; // Родительская категория

    @OneToMany(mappedBy = "parent")
    private List<Category> subcategories; // Список подкатегорий

    @OneToMany(mappedBy = "parentCategory")  // исправил mappedBy на parentCategory
    private List<Plant> plantsAsParentCategory; // Растения, где эта категория — родитель

    @OneToMany(mappedBy = "subcategory")      // ещё одно отношение
    private List<Plant> plantsAsSubcategory;   // Растения, где эта категория — подкатегория

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
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
}
