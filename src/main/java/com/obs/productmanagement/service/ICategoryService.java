package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.CategoryRequest;
import com.obs.productmanagement.dto.CategoryResponse;
import com.obs.productmanagement.model.Category;

import java.util.List;


public interface ICategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
