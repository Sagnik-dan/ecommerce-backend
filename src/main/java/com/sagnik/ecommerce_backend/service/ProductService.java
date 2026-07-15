package com.sagnik.ecommerce_backend.service;
import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllProducts(
            Pageable pageable);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    Page<ProductResponse> searchProducts(
            String keyword,
            Pageable pageable);
}
