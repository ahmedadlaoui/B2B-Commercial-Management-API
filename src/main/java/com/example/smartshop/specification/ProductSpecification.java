package com.example.smartshop.specification;

import com.example.smartshop.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), false);
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }

    public static Specification<Product> hasStockBetween(Integer minStock, Integer maxStock) {
        return (root, query, criteriaBuilder) -> {
            if (minStock == null && maxStock == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();

            if (minStock != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), minStock));
            }
            if (maxStock != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stock"), maxStock));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasKeyWord(String keyWord) {
        return (root, query, criteriaBuilder) -> {

            if (keyWord == null) {
                return null;
            }
            String pattern = "%" + keyWord.toLowerCase() + "%";

            Predicate nameLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    pattern);

            Predicate descriptionLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    pattern);

            return criteriaBuilder.or(nameLike, descriptionLike);
        };
    }

    public static Specification<Product> buildFilterSpecification(BigDecimal minPrice, BigDecimal maxPrice,
            Integer minStock, Integer maxStock, String keyWord) {

        Specification<Product> spec = Specification.where(isNotDeleted());

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(hasPriceBetween(minPrice, maxPrice));
        }

        if (minStock != null || maxStock != null) {
            spec = spec.and(hasStockBetween(minStock, maxStock));
        }

        if (keyWord != null) {
            spec = spec.and(hasKeyWord(keyWord));
        }

        return spec;
    }

}
