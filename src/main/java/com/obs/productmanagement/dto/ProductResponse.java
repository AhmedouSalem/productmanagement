package com.obs.productmanagement.dto;

import java.util.Date;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Date expiryDate,
        String categoryName
) {}