package com.mercado.mercadoSpring.repository.product;

import com.mercado.mercadoSpring.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    List<Product> findByCategory(String category);
    List<Product> findByDescription(String description);
    List<Product> findByCategoryAndDescription(String category, String description);
    List<Product> findByIsAvailableTrue();
    List<Product> findByIsAvailableFalse();
    List<Product> findByCategoryAndIsAvailableTrue(String category);
}
