package com.example.smartshop.service;

import com.example.smartshop.dto.product.ProductCreateRequest;
import com.example.smartshop.dto.product.ProductDTO;
import com.example.smartshop.entity.Product;
import com.example.smartshop.exception.DuplicateResourceException;
import com.example.smartshop.mapper.ProductMapper;
import com.example.smartshop.repository.ProductRepository;
import com.example.smartshop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - createProduct Tests")
class ProductServiceCreateProductTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductCreateRequest validRequest;
    private Product productEntity;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        validRequest = new ProductCreateRequest();
        validRequest.setName("Test Product");
        validRequest.setDescription("Test Description");
        validRequest.setPrice(new BigDecimal("100.00"));
        validRequest.setStock(50);

        productEntity = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .deleted(false)
                .build();

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(new BigDecimal("100.00"));
        productDTO.setStock(50);
    }

    @Test
    @DisplayName("Should successfully create product when valid request is provided")
    void createProduct_WithValidRequest_ShouldReturnProductDTO() {
        // Arrange
        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO.getId(), result.getId());
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getDescription(), result.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(result.getPrice()));
        assertEquals(productDTO.getStock(), result.getStock());

        // Verify interactions
        verify(productRepository, times(1)).existsByNameIgnoreCaseAndNotDeleted("Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when product with same name already exists")
    void createProduct_WithDuplicateName_ShouldThrowDuplicateResourceException() {
        // Arrange
        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> productService.createProduct(validRequest)
        );

        assertTrue(exception.getMessage().contains("Test Product"));
        assertTrue(exception.getMessage().contains("already exists"));

        // Verify that save was never called
        verify(productRepository, times(1)).existsByNameIgnoreCaseAndNotDeleted("Test Product");
        verify(productRepository, never()).save(any(Product.class));
        verify(productMapper, never()).toDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with null description")
    void createProduct_WithNullDescription_ShouldSucceed() {
        // Arrange
        validRequest.setDescription(null);
        productEntity.setDescription(null);
        productDTO.setDescription(null);

        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(validRequest);

        // Assert
        assertNotNull(result);
        assertNull(result.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with minimum valid price (0.01)")
    void createProduct_WithMinimumPrice_ShouldSucceed() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("0.01");
        validRequest.setPrice(minPrice);
        productEntity.setPrice(minPrice);
        productDTO.setPrice(minPrice);

        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(0, minPrice.compareTo(result.getPrice()));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should check for duplicate names case-insensitively")
    void createProduct_CaseInsensitiveNameCheck_ShouldCallRepositoryCorrectly() {
        // Arrange
        validRequest.setName("TeSt PrOdUcT");
        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        productService.createProduct(validRequest);

        // Assert
        verify(productRepository, times(1)).existsByNameIgnoreCaseAndNotDeleted("TeSt PrOdUcT");
    }

    @Test
    @DisplayName("Should set deleted flag to false when creating product")
    void createProduct_ShouldSetDeletedFlagToFalse() {
        // Arrange
        when(productRepository.existsByNameIgnoreCaseAndNotDeleted(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            assertFalse(savedProduct.getDeleted(), "Deleted flag should be false");
            return productEntity;
        });
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // Act
        productService.createProduct(validRequest);

        // Assert
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
