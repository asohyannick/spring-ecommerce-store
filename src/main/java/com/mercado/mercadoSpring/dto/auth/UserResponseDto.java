package com.mercado.mercadoSpring.dto.auth;
import java.util.Date;
public record UserResponseDto(
        String firstName,
        String lastName,
        String email,
        String role,
        Boolean isAccountBlocked,
        Date  createdAt,
        Date updatedAt
) { }
