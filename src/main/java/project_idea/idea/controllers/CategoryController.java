package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.Category;
import project_idea.idea.payloads.CategoryCreateDTO;
import project_idea.idea.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "APIs for category management")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private PagedResourcesAssembler<Category> pagedResourcesAssembler;

    @GetMapping
    public PagedModel<EntityModel<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Page<Category> categoryPage = categoryService.getAllCategories(page, size, sortBy);
        return pagedResourcesAssembler.toModel(categoryPage);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody CategoryCreateDTO categoryDTO) {
        return categoryService.createCategory(categoryDTO.name(), categoryDTO.description());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Category updateCategory(@PathVariable UUID id, @RequestBody CategoryCreateDTO categoryDTO) {
        return categoryService.updateCategory(id, categoryDTO.name(), categoryDTO.description());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
