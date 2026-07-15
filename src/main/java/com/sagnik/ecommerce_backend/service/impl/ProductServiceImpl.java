package com.sagnik.ecommerce_backend.service.impl;

import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.entity.Product;
import com.sagnik.ecommerce_backend.exception.CategoryNotFoundException;
import com.sagnik.ecommerce_backend.exception.ProductNotFoundException;
import com.sagnik.ecommerce_backend.repository.CategoryRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Category category =
                categoryRepository.findById(
                                request.getCategoryId())
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }
    @Override
    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found"));

        return mapToResponse(product);
    }
    @Override
    public Page<ProductResponse> getAllProducts(
            Pageable pageable) {

        Page<Product> products =
                productRepository.findAll(pageable);

        return products.map(this::mapToResponse);
    }
    private ProductResponse mapToResponse(
            Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .categoryId(
                        product.getCategory().getId()
                )
                .categoryName(
                        product.getCategory().getName()
                )
                .build();
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
    public Page<ProductResponse> searchProducts(
            String keyword,
            Pageable pageable) {

        return productRepository
                .findByNameContainingIgnoreCase(
                        keyword,
                        pageable)
                .map(this::mapToResponse);
    }
}
