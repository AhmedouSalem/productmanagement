package com.obs.productmanagement.repository;

import com.obs.productmanagement.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
    User findByNameAndPassword(String name, String password);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
