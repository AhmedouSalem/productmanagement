package com.obs.productmanagement.repository;

import com.obs.productmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsProductByName(String name);
    @Query("""
           SELECT p FROM Product p
           WHERE p.price = (SELECT MAX(p2.price) FROM Product p2)
           """)
    List<Product> findMostExpensiveProducts();

    // Tous les produits d'une catégorie
    List<Product> findByCategoryId(Long categoryId);

    // Produits les plus chers d'une catégorie
    @Query("""
           SELECT p FROM Product p
           WHERE p.category.id = :categoryId
             AND p.price = (
               SELECT MAX(p2.price) FROM Product p2 WHERE p2.category.id = :categoryId
           )
           """)
    List<Product> findMostExpensiveProductsByCategoryId(Long categoryId);
}
