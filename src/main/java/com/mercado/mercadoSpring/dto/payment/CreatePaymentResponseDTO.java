package com.mercado.mercadoSpring.dto.payment;
public record CreatePaymentResponseDTO(
        String paymentId,
        String paymentUrl,
        String status,
        String provider,
        String providerRef,
        String orderId,
        String stripeSessionId,
        String redirectUrl,
        String clientSecret
) {
}
