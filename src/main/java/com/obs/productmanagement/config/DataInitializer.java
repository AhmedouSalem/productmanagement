package com.obs.productmanagement.config;

import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.model.Product;
import com.obs.productmanagement.repository.CategoryRepository;
import com.obs.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        // -------- Categories --------
        Category electronics = categoryRepository.save(
                new Category(null, "Electronics", "Electronic devices", null)
        );
        Category fashion = categoryRepository.save(
                new Category(null, "Fashion", "Clothes and accessories", null)
        );
        Category grocery = categoryRepository.save(
                new Category(null, "Grocery", "Food and drinks", null)
        );

        // -------- Products --------
        productRepository.save(new Product(
                null,
                "MacBook Pro 16",
                2499.99,
                "High-end laptop",
                new Date(),
                electronics
        ));

        productRepository.save(new Product(
                null,
                "iPhone 15",
                1299.99,
                "Apple smartphone",
                new Date(),
                electronics
        ));

        productRepository.save(new Product(
                null,
                "AirPods",
                199.99,
                "Wireless earphones",
                new Date(),
                electronics
        ));

        productRepository.save(new Product(
                null,
                "Jacket",
                119.99,
                "Winter jacket",
                new Date(),
                fashion
        ));

        productRepository.save(new Product(
                null,
                "Shoes",
                89.99,
                "Running shoes",
                new Date(),
                fashion
        ));

        productRepository.save(new Product(
                null,
                "Coffee",
                8.50,
                "Ground coffee",
                new Date(),
                grocery
        ));
    }
}
