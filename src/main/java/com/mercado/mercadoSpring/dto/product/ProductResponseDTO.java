package com.mercado.mercadoSpring.dto.product;

public record ProductResponseDTO(
        Long id,
        String name,
        String brand,
        String category,
        Double price,
        Double rating,
        Boolean available,
        String imageURL
) {
}
