package com.example.smartshop.service;

import com.example.smartshop.dto.product.ProductCreateRequest;
import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.dto.product.ProductUpdateRequest;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductCreateRequest request);

    ProductDTO updateProduct(Long id, ProductUpdateRequest request);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    void softDeleteProduct(Long id);

    void restoreProduct(Long id);
}
