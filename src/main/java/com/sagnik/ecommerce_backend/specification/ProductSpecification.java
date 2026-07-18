package com.sagnik.ecommerce_backend.specification;

import com.sagnik.ecommerce_backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasKeyword(
            String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasCategory(
            Long categoryId) {

        return (root, query, cb) -> {

            if (categoryId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }
    public static Specification<Product> hasPriceBetween(
            BigDecimal min,
            BigDecimal max) {

        return (root, query, cb) -> {

            if (min == null && max == null) {
                return cb.conjunction();
            }

            if (min == null) {
                return cb.lessThanOrEqualTo(
                        root.get("price"),
                        max
                );
            }

            if (max == null) {
                return cb.greaterThanOrEqualTo(
                        root.get("price"),
                        min
                );
            }

            return cb.between(
                    root.get("price"),
                    min,
                    max
            );
        };
    }
}
