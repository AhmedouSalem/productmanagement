package com.obs.productmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        @NotBlank(message = "Name is mandatory")
        String name,
        @NotNull(message = "Age is mandatory")
        @Min(value = 12, message = "Age must be >= 12")
        Integer age,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email is not valid")
        String email,
        @NotBlank(message = "Password is mandatory")
        String password
) {}