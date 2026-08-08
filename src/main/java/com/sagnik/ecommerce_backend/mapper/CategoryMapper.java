package com.sagnik.ecommerce_backend.mapper;

import com.sagnik.ecommerce_backend.dto.CategoryRequest;
import com.sagnik.ecommerce_backend.dto.CategoryResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toEntity(CategoryRequest request) {

        return Category.builder()
                .name(request.getName())
                .build();
    }
}