package com.example.smartshop.controller;

import com.example.smartshop.dto.product.ProductCreateRequest;
import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.dto.product.ProductUpdateRequest;
import com.example.smartshop.service.ProductService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ProductDTO product = productService.createProduct(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");
        response.put("product", product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ProductDTO product = productService.updateProduct(id, request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product updated successfully");
        response.put("product", product);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireLogin(session);

        ProductDTO product = productService.getProductById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String search,
            HttpSession session) {

        authorizationUtil.requireLogin(session);

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> productPage = productService.getAllProducts(pageable, search);

        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("pageSize", productPage.getSize());
        response.put("hasNext", productPage.hasNext());
        response.put("hasPrevious", productPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> softDeleteProduct(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        productService.softDeleteProduct(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product soft-deleted successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restoreProduct(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        productService.restoreProduct(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product restored successfully");

        return ResponseEntity.ok(response);
    }
}
