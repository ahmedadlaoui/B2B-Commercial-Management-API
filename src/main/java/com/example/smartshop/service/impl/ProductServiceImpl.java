package com.example.smartshop.service.impl;

import com.example.smartshop.dto.product.ProductCreateRequest;
import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.dto.product.ProductUpdateRequest;
import com.example.smartshop.entity.Product;
import com.example.smartshop.exception.BusinessException;
import com.example.smartshop.exception.DuplicateResourceException;
import com.example.smartshop.exception.ResourceNotFoundException;
import com.example.smartshop.mapper.ProductMapper;
import com.example.smartshop.repository.ProductRepository;
import com.example.smartshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateRequest request) {
        // Check if product with same name already exists
        if (productRepository.existsByNameIgnoreCaseAndNotDeleted(request.getName())) {
            throw new DuplicateResourceException("Product with name '" + request.getName() + "' already exists");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .deleted(false)
                .build();

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Check if updating name would create a duplicate
        if (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) {
            if (productRepository.existsByNameIgnoreCaseAndNotDeletedExcludingId(request.getName(), id)) {
                throw new DuplicateResourceException("Product with name '" + request.getName() + "' already exists");
            }
        }

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByDeletedFalse();
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (!product.getDeleted()) {
            throw new BusinessException("Product is not deleted and cannot be restored");
        }

        // Check if restoring would create a name conflict
        if (productRepository.existsByNameIgnoreCaseAndNotDeleted(product.getName())) {
            throw new DuplicateResourceException(
                    "Cannot restore product: A product with name '" + product.getName() + "' already exists");
        }

        product.setDeleted(false);
        productRepository.save(product);
    }
}
