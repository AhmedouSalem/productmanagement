package com.obs.productmanagement.dto;

import java.util.Set;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Set<ProductResponse> products
) {
}
