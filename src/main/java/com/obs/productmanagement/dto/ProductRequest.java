package com.obs.productmanagement.dto;

import java.util.Date;

import jakarta.validation.constraints.*;

public record ProductRequest(
        Long id,

        @NotBlank(message = "Product name is mandatory")
        String name,

        @NotBlank(message = "Product description is mandatory")
        String description,

        @NotNull(message = "Price is mandatory")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
        Double price,

        @NotNull(message = "Expiry date is mandatory")
         @Future(message = "Expiry date must be in the future")
        Date expiryDate,

        @NotNull(message = "Category id is mandatory")
        @Positive(message = "Category id must be > 0")
        Long categoryId
) {}
