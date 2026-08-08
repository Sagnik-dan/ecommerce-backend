package com.sagnik.ecommerce_backend.mapper;

import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import com.sagnik.ecommerce_backend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public Product toEntity(
            ProductRequest request) {

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }
}