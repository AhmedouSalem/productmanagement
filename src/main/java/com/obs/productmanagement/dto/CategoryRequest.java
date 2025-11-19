package com.obs.productmanagement.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CategoryRequest(
        Long id,
        @NotBlank(message = "Name is mandatory")
        String name,
        @NotBlank(message = "Description is mandatory")
        String description
) {
}
