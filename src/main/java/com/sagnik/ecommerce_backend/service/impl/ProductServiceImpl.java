package com.sagnik.ecommerce_backend.service.impl;

import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.entity.Product;
import com.sagnik.ecommerce_backend.exception.CategoryNotFoundException;
import com.sagnik.ecommerce_backend.exception.ProductNotFoundException;
import com.sagnik.ecommerce_backend.mapper.ProductMapper;
import com.sagnik.ecommerce_backend.repository.CategoryRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.sagnik.ecommerce_backend.specification.ProductSpecification;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Category category =
                categoryRepository.findById(
                                request.getCategoryId())
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found"));

        Product product = productMapper.toEntity(request);

        product.setCategory(category);

        Product savedProduct =
                productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }
    private ProductResponse mapToResponse(
            Product product) {

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id,
                                         ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updated = productRepository.save(product);

        return mapToResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        productRepository.delete(product);
    }

    @Override
    public Page<ProductResponse> getProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        Specification<Product> specification =
                Specification
                        .where(ProductSpecification.hasKeyword(keyword))
                        .and(ProductSpecification.hasCategory(categoryId))
                        .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

        return productRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found"));

        return productMapper.toResponse(product);
    }
}
