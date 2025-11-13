package com.mercado.mercadoSpring.dto.product;

import jakarta.validation.constraints.*;

public class UpdateProductDto {
    private Long id;

    @NotBlank(message = "Image URL cannot be blank")
    private String imageURL;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Positive(message = "Price must be a positive value")
    private double price;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand cannot exceed 50 characters")
    private String brand;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable = true;

    @DecimalMin(value = "0.0", message = "Rating cannot be less than 0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5")
    private Double rating = 0.0;

    @PositiveOrZero(message = "Number of reviews cannot be negative")
    private Integer numberOfReviews = 0;

    @NotBlank(message = "Country of origin is required")
    @Size(max = 100, message = "Country of origin cannot exceed 100 characters")
    private String countryOfOrigin;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Size(max = 20, message = "Zip code cannot exceed 20 characters")
    private String zipCode;
}
