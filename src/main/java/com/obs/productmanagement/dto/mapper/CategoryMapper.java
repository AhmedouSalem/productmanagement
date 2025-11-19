package com.obs.productmanagement.dto.mapper;

import com.obs.productmanagement.dto.CategoryRequest;
import com.obs.productmanagement.dto.CategoryResponse;
import com.obs.productmanagement.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest dto);

    CategoryResponse toResponse(Category category);
}
