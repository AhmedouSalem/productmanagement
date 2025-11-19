package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.ProductRequest;
import com.obs.productmanagement.dto.ProductResponse;
import com.obs.productmanagement.dto.mapper.ProductMapper;
import com.obs.productmanagement.exception.CategoryNotFoundException;
import com.obs.productmanagement.exception.ProductAlreadyExistsException;
import com.obs.productmanagement.exception.ProductNotFoundException;
import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.model.Product;
import com.obs.productmanagement.repository.CategoryRepository;
import com.obs.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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

    private Product product;
    private Category category;
    private ProductRequest request;
    private ProductResponse response;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(10L);
        category.setName("Electronics");
        category.setDescription("Electronic devices");

        request = new ProductRequest(
                null,
                "iPhone",
                "Smartphone",
                999.99,
                new Date(),
                10L
        );

        product = new Product();
        product.setId(1L);
        product.setName("iPhone");
        product.setDescription("Smartphone");
        product.setPrice(999.99);
        product.setExpiryDate(request.expiryDate());
        product.setCategory(category);

        response = new ProductResponse(
                1L,
                "iPhone",
                "Smartphone",
                999.99,
                request.expiryDate(),
                "Electronics"
        );
    }

    @Test
    void getAllProducts_shouldReturnMappedList() {
        // GIVEN
        Product other = new Product();
        other.setId(2L);
        other.setName("TV");
        other.setDescription("Smart TV");
        other.setPrice(499.99);
        other.setExpiryDate(new Date());
        other.setCategory(category);

        ProductResponse response2 = new ProductResponse(
                2L,
                "TV",
                "Smart TV",
                499.99,
                other.getExpiryDate(),
                "Electronics"
        );

        when(productRepository.findAll()).thenReturn(List.of(product, other));
        when(productMapper.toResponse(product)).thenReturn(response);
        when(productMapper.toResponse(other)).thenReturn(response2);

        // WHEN
        List<ProductResponse> result = productService.getAllProducts();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("iPhone");
        assertThat(result.get(1).name()).isEqualTo("TV");

        verify(productRepository).findAll();
        verify(productMapper, times(2)).toResponse(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnResponse_whenProductExists() {
        // GIVEN
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        // WHEN
        ProductResponse result = productService.getProductById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("iPhone");

        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductById_shouldThrowException_whenProductDoesNotExist() {
        // GIVEN
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(1L);
        verifyNoInteractions(productMapper);
    }

    @Test
    void createProduct_shouldSaveAndReturnResponse_whenNameDoesNotExist_andCategoryExists() {
        // GIVEN
        when(productRepository.existsProductByName(request.name())).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        // WHEN
        ProductResponse result = productService.createProduct(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("Electronics");

        verify(productRepository).existsProductByName("iPhone");
        verify(productMapper).toEntity(request);
        verify(categoryRepository).findById(10L);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void createProduct_shouldThrowException_whenProductNameAlreadyExists() {
        // GIVEN
        when(productRepository.existsProductByName(request.name())).thenReturn(true);

        // WHEN + THEN
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ProductAlreadyExistsException.class);

        verify(productRepository).existsProductByName("iPhone");
        verify(productRepository, never()).save(any());
        verifyNoInteractions(categoryRepository, productMapper);
    }

    @Test
    void createProduct_shouldThrowException_whenCategoryDoesNotExist() {
        // GIVEN
        when(productRepository.existsProductByName(request.name())).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository).existsProductByName("iPhone");
        verify(productMapper).toEntity(request);
        verify(categoryRepository).findById(10L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_shouldUpdateAndReturnResponse_whenProductAndCategoryExist() {
        // GIVEN
        ProductRequest updateRequest = new ProductRequest(
                null,
                "UpdatedName",
                "Updated desc",
                899.99,
                new Date(),
                10L
        );

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("UpdatedName");
        updated.setDescription("Updated desc");
        updated.setPrice(899.99);
        updated.setExpiryDate(updateRequest.expiryDate());
        updated.setCategory(category);

        ProductResponse updatedResponse = new ProductResponse(
                1L,
                "UpdatedName",
                "Updated desc",
                899.99,
                updateRequest.expiryDate(),
                "Electronics"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(updatedResponse);

        // WHEN
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // THEN
        assertThat(result.name()).isEqualTo("UpdatedName");
        assertThat(result.price()).isEqualTo(899.99);

        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(10L);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(updated);
    }

    @Test
    void updateProduct_shouldThrowException_whenProductDoesNotExist() {
        // GIVEN
        ProductRequest updateRequest = new ProductRequest(
                null,
                "UpdatedName",
                "Updated desc",
                899.99,
                new Date(),
                10L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> productService.updateProduct(1L, updateRequest))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(1L);
        verifyNoInteractions(categoryRepository, productMapper);
    }

    @Test
    void updateProduct_shouldThrowException_whenCategoryDoesNotExist_andCategoryIdNotNull() {
        // GIVEN
        ProductRequest updateRequest = new ProductRequest(
                null,
                "UpdatedName",
                "Updated desc",
                899.99,
                new Date(),
                10L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> productService.updateProduct(1L, updateRequest))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(10L);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(productMapper);
    }

    @Test
    void updateProduct_shouldUpdateWithoutChangingCategory_whenCategoryIdIsNull() {
        // GIVEN
        // on part d'un produit déjà associé à une catégorie
        product.setCategory(category);

        ProductRequest updateRequest = new ProductRequest(
                null,
                "UpdatedName",
                "Updated desc",
                899.99,
                new Date(),
                null // 👈 on NE change PAS la catégorie
        );

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("UpdatedName");
        updated.setDescription("Updated desc");
        updated.setPrice(899.99);
        updated.setExpiryDate(updateRequest.expiryDate());
        // la catégorie doit rester la même
        updated.setCategory(category);

        ProductResponse updatedResponse = new ProductResponse(
                1L,
                "UpdatedName",
                "Updated desc",
                899.99,
                updateRequest.expiryDate(),
                "Electronics"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // ici, comme categoryId est null, categoryRepository NE DOIT PAS être appelé
        when(productRepository.save(product)).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(updatedResponse);

        // WHEN
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // THEN
        assertThat(result.name()).isEqualTo("UpdatedName");
        assertThat(result.description()).isEqualTo("Updated desc");
        assertThat(result.categoryName()).isEqualTo("Electronics");

        // on vérifie bien le flux !
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(updated);

        // et surtout : aucune interaction avec categoryRepository
        verifyNoInteractions(categoryRepository);

        // et on peut vérifier que la catégorie de l'entity n'a pas été changée
        assertThat(product.getCategory()).isEqualTo(category);
    }


    @Test
    void deleteProduct_shouldDelete_whenProductExists() {
        // GIVEN
        when(productRepository.existsById(1L)).thenReturn(true);

        // WHEN
        productService.deleteProduct(1L);

        // THEN
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductDoesNotExist() {
        // GIVEN
        when(productRepository.existsById(1L)).thenReturn(false);

        // WHEN + THEN
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void getMostExpensiveProducts_shouldReturnMappedListOfMostExpensiveProducts() {
        // GIVEN
        Category cat = category;
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("MacBook Pro");
        p1.setDescription("Laptop");
        p1.setPrice(2499.99);
        p1.setExpiryDate(new Date());
        p1.setCategory(cat);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Gaming PC");
        p2.setDescription("High-end PC");
        p2.setPrice(2499.99); // même prix max
        p2.setExpiryDate(new Date());
        p2.setCategory(cat);

        ProductResponse r1 = new ProductResponse(
                1L,
                "MacBook Pro",
                "Laptop",
                2499.99,
                p1.getExpiryDate(),
                "Electronics"
        );

        ProductResponse r2 = new ProductResponse(
                2L,
                "Gaming PC",
                "High-end PC",
                2499.99,
                p2.getExpiryDate(),
                "Electronics"
        );

        when(productRepository.findMostExpensiveProducts()).thenReturn(List.of(p1, p2));
        when(productMapper.toResponse(p1)).thenReturn(r1);
        when(productMapper.toResponse(p2)).thenReturn(r2);

        // WHEN
        List<ProductResponse> result = productService.getMostExpensiveProducts();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("MacBook Pro");
        assertThat(result.get(1).name()).isEqualTo("Gaming PC");

        verify(productRepository).findMostExpensiveProducts();
        verify(productMapper).toResponse(p1);
        verify(productMapper).toResponse(p2);
    }

    @Test
    void getProductsByCategory_shouldReturnMappedProducts_whenCategoryExists() {
        Long categoryId = 10L;

        // GIVEN
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("iPhone");
        p1.setDescription("Smartphone");
        p1.setPrice(999.99);
        p1.setExpiryDate(new Date());
        p1.setCategory(category);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("MacBook");
        p2.setDescription("Laptop");
        p2.setPrice(1999.99);
        p2.setExpiryDate(new Date());
        p2.setCategory(category);

        ProductResponse r1 = new ProductResponse(
                1L, "iPhone", "Smartphone", 999.99, p1.getExpiryDate(), "Electronics"
        );
        ProductResponse r2 = new ProductResponse(
                2L, "MacBook", "Laptop", 1999.99, p2.getExpiryDate(), "Electronics"
        );

        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(p1, p2));
        when(productMapper.toResponse(p1)).thenReturn(r1);
        when(productMapper.toResponse(p2)).thenReturn(r2);

        // WHEN
        List<ProductResponse> result = productService.getProductsByCategory(categoryId);

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("iPhone");
        assertThat(result.get(1).name()).isEqualTo("MacBook");

        verify(categoryRepository).existsById(categoryId);
        verify(productRepository).findByCategoryId(categoryId);
        verify(productMapper).toResponse(p1);
        verify(productMapper).toResponse(p2);
    }

    @Test
    void getProductsByCategory_shouldThrowCategoryNotFound_whenCategoryDoesNotExist() {
        Long categoryId = 999L;

        // GIVEN
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // WHEN + THEN
        assertThatThrownBy(() -> productService.getProductsByCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).existsById(categoryId);
        verifyNoInteractions(productRepository, productMapper);
    }

    @Test
    void getMostExpensiveProductsByCategory_shouldReturnMappedProducts_whenCategoryExists() {
        Long categoryId = 10L;

        // GIVEN
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("MacBook Pro");
        p1.setDescription("Laptop");
        p1.setPrice(2499.99);
        p1.setExpiryDate(new Date());
        p1.setCategory(category);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Gaming PC");
        p2.setDescription("High-end PC");
        p2.setPrice(2499.99); // même prix max
        p2.setExpiryDate(new Date());
        p2.setCategory(category);

        ProductResponse r1 = new ProductResponse(
                1L, "MacBook Pro", "Laptop", 2499.99, p1.getExpiryDate(), "Electronics"
        );
        ProductResponse r2 = new ProductResponse(
                2L, "Gaming PC", "High-end PC", 2499.99, p2.getExpiryDate(), "Electronics"
        );

        when(productRepository.findMostExpensiveProductsByCategoryId(categoryId))
                .thenReturn(List.of(p1, p2));
        when(productMapper.toResponse(p1)).thenReturn(r1);
        when(productMapper.toResponse(p2)).thenReturn(r2);

        // WHEN
        List<ProductResponse> result = productService.getMostExpensiveProductsByCategory(categoryId);

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("MacBook Pro");
        assertThat(result.get(1).name()).isEqualTo("Gaming PC");

        verify(categoryRepository).existsById(categoryId);
        verify(productRepository).findMostExpensiveProductsByCategoryId(categoryId);
        verify(productMapper).toResponse(p1);
        verify(productMapper).toResponse(p2);
    }

    @Test
    void getMostExpensiveProductsByCategory_shouldThrowCategoryNotFound_whenCategoryDoesNotExist() {
        Long categoryId = 999L;

        // GIVEN
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // WHEN + THEN
        assertThatThrownBy(() -> productService.getMostExpensiveProductsByCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).existsById(categoryId);
        verifyNoInteractions(productRepository, productMapper);
    }
}
