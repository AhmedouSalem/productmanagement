package com.obs.productmanagement.repository;

import com.obs.productmanagement.model.Category;
import com.obs.productmanagement.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void existsProductByName_shouldReturnTrue_whenProductWithNameExists() {
        // GIVEN
        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic devices");
        Category savedCategory = categoryRepository.save(category);

        Product product = new Product();
        product.setName("iPhone");
        product.setDescription("Smartphone");
        product.setPrice(999.99);
        product.setExpiryDate(new Date());
        product.setCategory(savedCategory);
        productRepository.save(product);

        // WHEN
        boolean exists = productRepository.existsProductByName("iPhone");

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void existsProductByName_shouldReturnFalse_whenNoProductWithName() {
        // WHEN
        boolean exists = productRepository.existsProductByName("UnknownProduct");

        // THEN
        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnProduct_whenIdExists() {
        // GIVEN
        Category category = new Category();
        category.setName("Books");
        category.setDescription("All books");
        Category savedCategory = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Clean Code");
        product.setDescription("Programming book");
        product.setPrice(39.99);
        product.setExpiryDate(new Date());
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);

        // WHEN
        Optional<Product> found = productRepository.findById(savedProduct.getId());

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Clean Code");
    }

    @Test
    void findById_shouldBeEmpty_whenIdDoesNotExist() {
        // WHEN
        Optional<Product> found = productRepository.findById(999L);

        // THEN
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveProduct_whenIdExists() {
        // GIVEN
        Category category = new Category();
        category.setName("Games");
        category.setDescription("Video games");
        Category savedCategory = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Zelda");
        product.setDescription("Switch game");
        product.setPrice(59.99);
        product.setExpiryDate(new Date());
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);

        Long id = savedProduct.getId();
        assertThat(productRepository.existsById(id)).isTrue();

        // WHEN
        productRepository.deleteById(id);

        // THEN
        assertThat(productRepository.existsById(id)).isFalse();
    }
}
