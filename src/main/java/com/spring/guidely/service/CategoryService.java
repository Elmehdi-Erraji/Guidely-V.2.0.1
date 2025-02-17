package com.spring.guidely.service;

import com.spring.guidely.entities.Category;
import com.spring.guidely.repository.CategoryRepository;
import com.spring.guidely.web.error.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }


    public Category updateCategory(UUID id, Category category) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(UUID id) {
        Category existingCategory = getCategoryById(id);
        categoryRepository.delete(existingCategory);
    }
}
