package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Category;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Category with name " + name + " already exists");
        }
        
        Category category = new Category(name, description, false);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public void deleteCategory(UUID id) {
        Category category = getCategoryById(id);
        if (category.isSystemCategory()) {
            throw new BadRequestException("Cannot delete system categories");
        }
        categoryRepository.delete(category);
    }

    public Category updateCategory(UUID id, String name, String description) {
        Category category = getCategoryById(id);
        if (category.isSystemCategory()) {
            throw new BadRequestException("Cannot modify system categories");
        }
        
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new BadRequestException("Category with name " + name + " already exists");
        }
        
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }
}
