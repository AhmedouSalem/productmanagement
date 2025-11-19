package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.CategoryRequest;
import com.obs.productmanagement.dto.CategoryResponse;
import com.obs.productmanagement.dto.mapper.CategoryMapper;
import com.obs.productmanagement.exception.CategoryAlreadyExistsException;
import com.obs.productmanagement.exception.CategoryNotFoundException;
import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException(request.name());
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        existing.setName(request.name());
        existing.setDescription(request.description());

        Category updated = categoryRepository.save(existing);

        return categoryMapper.toResponse(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }

        categoryRepository.deleteById(id);
    }
}
