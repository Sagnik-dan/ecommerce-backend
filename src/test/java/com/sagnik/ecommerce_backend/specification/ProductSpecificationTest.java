package com.sagnik.ecommerce_backend.specification;

import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

class ProductSpecificationTest {

    @Test
    void hasKeyword_shouldCreateSpecification() {

        Specification<Product> specification =
                ProductSpecification.hasKeyword("phone");

        assertNotNull(specification);
    }
}