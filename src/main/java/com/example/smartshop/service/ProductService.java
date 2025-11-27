package com.example.smartshop.service;

import com.example.smartshop.dto.product.ProductCreateRequest;
import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.dto.product.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductCreateRequest request);

    ProductDTO updateProduct(Long id, ProductUpdateRequest request);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    Page<ProductDTO> getAllProducts(Pageable pageable, String search);

    void softDeleteProduct(Long id);

    void restoreProduct(Long id);
}
