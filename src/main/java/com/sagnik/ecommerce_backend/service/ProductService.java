package com.sagnik.ecommerce_backend.service;
import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    Page<ProductResponse> getProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

    ProductResponse getProduct(Long id);
}
