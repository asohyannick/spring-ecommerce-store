package com.mercado.mercadoSpring.entity.product;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageURL;

    private String name;

    @Column(length = 2000)
    private String description;

    private double price;

    private String category;

    private String brand;

    private int stockQuantity;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    private Double rating;

    private int numberOfReviews;

    private String countryOfOrigin;

    private String city;

    private String state;

    private String zipCode;
}
