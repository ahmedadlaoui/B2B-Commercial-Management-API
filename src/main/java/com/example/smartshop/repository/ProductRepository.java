package com.example.smartshop.repository;

import com.example.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all non-deleted products
    List<Product> findByDeletedFalse();

    // Find product by ID only if not deleted
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findByIdAndNotDeleted(Long id);

    // Find product by name (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name) AND p.deleted = false")
    Optional<Product> findByNameIgnoreCaseAndNotDeleted(String name);

    // Check if product exists by name (excluding specific ID for updates)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE LOWER(p.name) = LOWER(:name) AND p.deleted = false AND p.id != :excludeId")
    boolean existsByNameIgnoreCaseAndNotDeletedExcludingId(String name, Long excludeId);

    // Check if product exists by name
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE LOWER(p.name) = LOWER(:name) AND p.deleted = false")
    boolean existsByNameIgnoreCaseAndNotDeleted(String name);
}
