package com.obs.productmanagement.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}