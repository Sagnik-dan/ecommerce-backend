package com.sagnik.ecommerce_backend.service.impl;


import com.sagnik.ecommerce_backend.dto.CategoryRequest;
import com.sagnik.ecommerce_backend.dto.CategoryResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.exception.CategoryAlreadyExistsException;
import com.sagnik.ecommerce_backend.exception.CategoryNotFoundException;
import com.sagnik.ecommerce_backend.mapper.CategoryMapper;
import com.sagnik.ecommerce_backend.repository.CategoryRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(
                request.getName().trim())) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists"
            );
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"));

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"));

        String categoryName = request.getName().trim();

        if (!category.getName().equalsIgnoreCase(categoryName)
                && categoryRepository.existsByNameIgnoreCase(
                categoryName)) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists");
        }

        category.setName(categoryName);

        Category savedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"));

        if (productRepository.existsByCategoryId(id)) {

            throw new IllegalStateException(
                    "Cannot delete category containing products");
        }

        categoryRepository.delete(category);
    }
}
