package com.mercado.mercadoSpring.dto.cart;

import com.mercado.mercadoSpring.dto.cartItem.CartItemResponse;
import java.math.BigDecimal;
import java.util.Set;

public record CartResponse(
        Long cartId,
        Set<CartItemResponse> items,
        int totalItems,
        BigDecimal totalAmount
) {}
