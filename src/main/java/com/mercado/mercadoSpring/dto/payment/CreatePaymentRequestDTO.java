package com.mercado.mercadoSpring.dto.payment;
import com.mercado.mercadoSpring.constants.currency.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record CreatePaymentRequestDTO(
        @NotBlank String orderId,
        @NotBlank String userId,
        @NotNull Currency currency,
        @Positive long amount,
        @NotBlank String productName
) { }
