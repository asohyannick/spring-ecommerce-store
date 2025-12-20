package com.mercado.mercadoSpring.dto.payment;
public record CreatePaymentResponseDTO(
        String paymentId,
        String clientSecret,
        String status,
        String provider,
        String providerRef
) {}
