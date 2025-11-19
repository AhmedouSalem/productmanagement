package com.obs.productmanagement.repository;

import com.obs.productmanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrue_whenUserWithEmailExists() {
        // GIVEN
        User user = new User();
        user.setName("Salem");
        user.setAge(25);
        user.setEmail("salem@test.com");
        user.setPassword("secret");
        userRepository.save(user);

        // WHEN
        boolean exists = userRepository.existsByEmail("salem@test.com");

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void findByEmailAndPassword_shouldReturnUser_whenCredentialsMatch() {
        // GIVEN
        User user = new User();
        user.setName("Salem");
        user.setAge(25);
        user.setEmail("salem@test.com");
        user.setPassword("secret");
        userRepository.save(user);

        // WHEN
        User found = userRepository.findByEmailAndPassword("salem@test.com", "secret");

        // THEN
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("salem@test.com");
    }

    @Test
    void findByNameAndPassword_shouldReturnUser_whenCredentialsMatch() {
        // GIVEN
        User user = new User();
        user.setName("salem");
        user.setAge(25);
        user.setEmail("salem@test.com");
        user.setPassword("secret");
        userRepository.save(user);

        // WHEN
        User found = userRepository.findByNameAndPassword("salem", "secret");

        // THEN
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("salem");
    }
}
