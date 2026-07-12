package com.sagnik.ecommerce_backend.service.impl;


import com.sagnik.ecommerce_backend.dto.CategoryRequest;
import com.sagnik.ecommerce_backend.dto.CategoryResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.exception.CategoryAlreadyExistsException;
import com.sagnik.ecommerce_backend.repository.CategoryRepository;
import com.sagnik.ecommerce_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists"
            );
        }

        String categoryName = request.getName().trim();

        Category category = Category.builder()
                .name(categoryName)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
