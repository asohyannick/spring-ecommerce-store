package com.mercado.mercadoSpring.repository.product;

import com.mercado.mercadoSpring.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) " +
            "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:isAvailable IS NULL OR p.available = :available) " +
            "AND (:minRating IS NULL OR p.rating >= :minRating) " +
            "AND (:maxRating IS NULL OR p.rating <= :maxRating)")
    List<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("brand") String brand,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("isAvailable") Boolean isAvailable,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating
    );

    // Existing helper methods
    @Query("SELECT p FROM Product p WHERE LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Product> findByCategory(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Product> findByDescriptionContaining(@Param("description") String description);

    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR LOWER(TRIM(p.category)) LIKE LOWER(CONCAT('%', TRIM(:category), '%'))) " +
            "AND (:description IS NULL OR LOWER(TRIM(p.description)) LIKE LOWER(CONCAT('%', TRIM(:description), '%')))")
    List<Product> findByCategoryAndDescription(@Param("category") String category,
                                               @Param("description") String description);
   //  @Query("SELECT p FROM Product p WHERE LOWER(p.isAvailable) LIKE LOWER(CONCAT('%', :isAvailable, '%'))")
    List<Product> findByAvailableTrue();

//    @Query("SELECT p FROM Product p WHERE LOWER(p.isAvailable) LIKE LOWER(CONCAT('%', :isAvailable, '%'))")
    List<Product> findByAvailableFalse();
    List<Product> findByCategoryAndAvailableTrue(String category);

    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
          AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:available IS NULL OR p.available = :available)
          AND (:minRating IS NULL OR p.rating >= :minRating)
          AND (:maxRating IS NULL OR p.rating <= :maxRating)
    """)
    Page<Product> filterProducts(
            @Param("keyword") String keyword,
            @Param("brand") String brand,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("available") Boolean available,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            Pageable pageable
    );

}
