package com.obs.productmanagement.controller;

import com.obs.productmanagement.dto.ProductRequest;
import com.obs.productmanagement.dto.ProductResponse;
import com.obs.productmanagement.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final IProductService productService;

    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @Min(value = 1, message = "Id must be >= 1") Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        URI location = URI.create("/api/products/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable @Min(value = 1, message = "Id must be >= 1") Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Min(value = 1, message = "Id must be >= 1") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/products/most-expensive
    @GetMapping("/most-expensive")
    public ResponseEntity<List<ProductResponse>> getMostExpensiveProducts() {
        List<ProductResponse> products = productService.getMostExpensiveProducts();
        return ResponseEntity.ok(products);
    }

    // GET /api/products/by-category/{categoryId}
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable @Positive(message = "Category id must be > 0") Long categoryId
    ) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    // GET /api/products/by-category/{categoryId}/most-expensive
    @GetMapping("/by-category/{categoryId}/most-expensive")
    public ResponseEntity<List<ProductResponse>> getMostExpensiveProductsByCategory(
            @PathVariable @Positive(message = "Category id must be > 0") Long categoryId
    ) {
        List<ProductResponse> products = productService.getMostExpensiveProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
}

