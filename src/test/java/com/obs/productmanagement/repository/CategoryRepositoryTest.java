package com.obs.productmanagement.repository;

import com.obs.productmanagement.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void existsByName_shouldReturnTrue_whenCategoryWithNameExists() {
        // GIVEN
        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic devices");
        categoryRepository.save(category);

        // WHEN
        boolean exists = categoryRepository.existsByName("Electronics");

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_shouldReturnFalse_whenCategoryWithNameDoesNotExist() {
        // WHEN
        boolean exists = categoryRepository.existsByName("Unknown");

        // THEN
        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnCategory_whenExistingId() {
        // GIVEN
        Category category = new Category();
        category.setName("Books");
        category.setDescription("All kinds of books");
        Category saved = categoryRepository.save(category);

        // WHEN
        Optional<Category> found = categoryRepository.findById(saved.getId());

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Books");
    }

    @Test
    void findById_shouldBeEmpty_whenNonExistingId() {
        // WHEN
        Optional<Category> found = categoryRepository.findById(999L);

        // THEN
        assertThat(found).isEmpty();
    }
}
