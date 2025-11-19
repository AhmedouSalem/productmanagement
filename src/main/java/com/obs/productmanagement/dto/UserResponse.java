package com.obs.productmanagement.dto;

public record UserResponse(
        Long id,
        String name,
        Integer age,
        String email
) {}
