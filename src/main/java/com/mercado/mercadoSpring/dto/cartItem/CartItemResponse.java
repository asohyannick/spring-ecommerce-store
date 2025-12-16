package com.mercado.mercadoSpring.dto.cartItem;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
@Getter
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long cartId;
    private Long productId;
    private CartItem cartItem;
    private int quantity;
    private BigDecimal subtotal;
    private BigDecimal unitPrice;
}
