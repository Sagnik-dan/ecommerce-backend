package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.CategoryRequest;
import com.sagnik.ecommerce_backend.dto.CategoryResponse;
import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import com.sagnik.ecommerce_backend.service.CategoryService;
import com.sagnik.ecommerce_backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id);
    }

    @GetMapping
    public Page<ProductResponse> getAllProducts(
            Pageable pageable) {

        return productService.getAllProducts(pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public Page<ProductResponse> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {

        return productService.searchProducts(
                keyword,
                pageable);
    }
}