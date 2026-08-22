package service.impl;

import com.sagnik.ecommerce_backend.dto.ProductRequest;
import com.sagnik.ecommerce_backend.dto.ProductResponse;
import com.sagnik.ecommerce_backend.entity.Category;
import com.sagnik.ecommerce_backend.entity.Product;
import com.sagnik.ecommerce_backend.exception.CategoryNotFoundException;
import com.sagnik.ecommerce_backend.exception.ProductNotFoundException;
import com.sagnik.ecommerce_backend.mapper.ProductMapper;
import com.sagnik.ecommerce_backend.repository.CategoryRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .category(category)
                .version(0L)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .categoryId(1L)
                .build();
    }

    @Test
    void createProduct_shouldCreateProductSuccessfully() {

        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(10);
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse response =
                productService.createProduct(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(
                new BigDecimal("100.00"),
                response.getPrice()
        );
        assertEquals(10, response.getStock());
        assertEquals(1L, response.getCategoryId());

        verify(categoryRepository).findById(1L);
        verify(productMapper).toEntity(request);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void createProduct_shouldThrowException_whenCategoryDoesNotExist() {

        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(10);
        request.setCategoryId(99L);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toEntity(any());
    }
    @Test
    void getProduct_shouldReturnProduct_whenProductExists() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse response =
                productService.getProduct(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(
                new BigDecimal("100.00"),
                response.getPrice()
        );
        assertEquals(10, response.getStock());

        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProduct_shouldThrowException_whenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProduct(99L)
        );

        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void updateProduct_shouldUpdateProductSuccessfully() {

        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("150.00"));
        request.setStock(20);
        request.setImageUrl("updated-image.jpg");
        request.setCategoryId(1L);

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("150.00"))
                .stock(20)
                .categoryId(1L)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(updatedResponse);

        ProductResponse response =
                productService.updateProduct(1L, request);

        assertNotNull(response);

        assertEquals("Updated Product", product.getName());
        assertEquals("Updated Description", product.getDescription());
        assertEquals(
                new BigDecimal("150.00"),
                product.getPrice()
        );
        assertEquals(20, product.getStock());
        assertEquals("updated-image.jpg", product.getImageUrl());
        assertEquals(category, product.getCategory());

        assertEquals("Updated Product", response.getName());
        assertEquals(
                new BigDecimal("150.00"),
                response.getPrice()
        );

        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void updateProduct_shouldThrowException_whenProductDoesNotExist() {

        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setPrice(new BigDecimal("150.00"));
        request.setStock(20);
        request.setCategoryId(1L);

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(99L, request)
        );

        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_shouldThrowException_whenCategoryDoesNotExist() {

        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("150.00"));
        request.setStock(20);
        request.setCategoryId(99L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.updateProduct(1L, request)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenProductExists() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getProducts_shouldReturnPagedProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(productRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        Page<ProductResponse> response =
                productService.getProducts(
                        null,
                        null,
                        null,
                        null,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        assertEquals(
                "Test Product",
                response.getContent().get(0).getName()
        );

        verify(productRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );

        verify(productMapper).toResponse(product);
    }

    @Test
    void getProducts_shouldApplyFilters() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(productRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        Page<ProductResponse> response =
                productService.getProducts(
                        "Test",
                        1L,
                        new BigDecimal("50.00"),
                        new BigDecimal("150.00"),
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Test Product",
                response.getContent().get(0).getName());

        verify(productRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );

        verify(productMapper).toResponse(product);
    }
}