package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.ProductRequest;
import com.obs.productmanagement.dto.ProductResponse;
import com.obs.productmanagement.dto.mapper.ProductMapper;
import com.obs.productmanagement.exception.CategoryNotFoundException;
import com.obs.productmanagement.exception.ProductAlreadyExistsException;
import com.obs.productmanagement.exception.ProductNotFoundException;
import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.model.Product;
import com.obs.productmanagement.repository.CategoryRepository;
import com.obs.productmanagement.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsProductByName(request.name())) {
            throw new ProductAlreadyExistsException(request.name());
        }

        Product product = productMapper.toEntity(request);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        // Vérifier que le produit existe
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Mettre à jour les champs simples
        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setPrice(request.price());
        existing.setExpiryDate(request.expiryDate());

        // Mettre à jour la catégorie si besoin
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
            existing.setCategory(category);
        }

        Product updated = productRepository.save(existing);

        return productMapper.toResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getMostExpensiveProducts() {
        return productRepository.findMostExpensiveProducts()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getMostExpensiveProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        return productRepository.findMostExpensiveProductsByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
