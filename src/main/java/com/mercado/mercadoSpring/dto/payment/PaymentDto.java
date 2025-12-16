package com.mercado.mercadoSpring.dto.payment;
import com.mercado.mercadoSpring.constants.currency.Currency;
import com.mercado.mercadoSpring.constants.paymentProvider.PaymentProvider;
import com.mercado.mercadoSpring.constants.paymentStatus.PaymentStatus;
import java.time.Instant;
public record PaymentDto(
        String id,
        String orderId,
        String userId,
        Currency currency,
        long amount,
        PaymentStatus status,
        PaymentProvider provider,
        String providerRef,
        Instant createdAt
) {
}
