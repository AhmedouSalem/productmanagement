package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.ProductRequest;
import com.obs.productmanagement.dto.ProductResponse;
import com.obs.productmanagement.model.Product;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> getMostExpensiveProducts();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> getMostExpensiveProductsByCategory(Long categoryId);
}
