package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.CategoryRequest;
import com.obs.productmanagement.dto.CategoryResponse;
import com.obs.productmanagement.dto.mapper.CategoryMapper;
import com.obs.productmanagement.exception.CategoryAlreadyExistsException;
import com.obs.productmanagement.exception.CategoryNotFoundException;
import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        request = new CategoryRequest(
                null,
                "Electronics",
                "Electronic devices"
        );

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic devices");

        response = new CategoryResponse(
                1L,
                "Electronics",
                "Electronic devices",
                Set.of()  // pas de produits pour ce test
        );
    }

    @Test
    void getAllCategories_shouldReturnListOfResponses() {
        // GIVEN
        Category another = new Category();
        another.setId(2L);
        another.setName("Books");
        another.setDescription("All kinds of books");

        CategoryResponse response2 = new CategoryResponse(
                2L,
                "Books",
                "All kinds of books",
                Set.of()
        );

        when(categoryRepository.findAll()).thenReturn(List.of(category, another));
        when(categoryMapper.toResponse(category)).thenReturn(response);
        when(categoryMapper.toResponse(another)).thenReturn(response2);

        // WHEN
        List<CategoryResponse> result = categoryService.getAllCategories();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Electronics");
        assertThat(result.get(1).name()).isEqualTo("Books");

        verify(categoryRepository).findAll();
        verify(categoryMapper, times(2)).toResponse(any(Category.class));
    }

    @Test
    void getCategoryById_shouldReturnResponse_whenCategoryExists() {
        // GIVEN
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        // WHEN
        CategoryResponse result = categoryService.getCategoryById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Electronics");

        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getCategoryById_shouldThrowException_whenCategoryDoesNotExist() {
        // GIVEN
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).findById(1L);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void createCategory_shouldSaveAndReturnResponse_whenNameDoesNotExist() {
        // GIVEN
        when(categoryRepository.existsByName(request.name())).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        // WHEN
        CategoryResponse result = categoryService.createCategory(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Electronics");

        verify(categoryRepository).existsByName("Electronics");
        verify(categoryMapper).toEntity(request);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void createCategory_shouldThrowException_whenNameAlreadyExists() {
        // GIVEN
        when(categoryRepository.existsByName(request.name())).thenReturn(true);

        // WHEN + THEN
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void updateCategory_shouldUpdateAndReturnResponse_whenCategoryExists() {
        // GIVEN
        CategoryRequest updateRequest = new CategoryRequest(
                null,
                "UpdatedName",
                "Updated description"
        );

        Category updated = new Category();
        updated.setId(1L);
        updated.setName("UpdatedName");
        updated.setDescription("Updated description");

        CategoryResponse updatedResponse = new CategoryResponse(
                1L,
                "UpdatedName",
                "Updated description",
                Set.of()
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(updated);
        when(categoryMapper.toResponse(updated)).thenReturn(updatedResponse);

        // WHEN
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // THEN
        assertThat(result.name()).isEqualTo("UpdatedName");
        assertThat(result.description()).isEqualTo("Updated description");

        // Vérifier que l'entity a été modifiée avant save
        assertThat(category.getName()).isEqualTo("UpdatedName");
        assertThat(category.getDescription()).isEqualTo("Updated description");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(updated);
    }

    @Test
    void updateCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // GIVEN
        CategoryRequest updateRequest = new CategoryRequest(
                null,
                "UpdatedName",
                "Updated description"
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void deleteCategory_shouldDelete_whenCategoryExists() {
        // GIVEN
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // WHEN
        categoryService.deleteCategory(1L);

        // THEN
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // GIVEN
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // WHEN + THEN
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).existsById(1L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
