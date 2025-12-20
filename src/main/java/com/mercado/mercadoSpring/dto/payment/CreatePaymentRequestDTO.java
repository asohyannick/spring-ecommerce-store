package com.mercado.mercadoSpring.dto.payment;
public record CreatePaymentRequestDTO(
        Long amount,
        String currency,
        Long orderId,
        Long customerId
) {}
