package com.obs.productmanagement.dto.mapper;

import com.obs.productmanagement.dto.ProductRequest;
import com.obs.productmanagement.dto.ProductResponse;
import com.obs.productmanagement.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // On ignore category : on la settra dans le service après avoir fetch la Category
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest dto);

    // Ici on peut renvoyer juste le nom de la catégorie dans la réponse
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);
}
