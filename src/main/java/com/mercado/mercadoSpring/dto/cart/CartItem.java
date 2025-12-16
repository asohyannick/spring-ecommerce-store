package com.mercado.mercadoSpring.dto.cart;
import com.mercado.mercadoSpring.dto.cartItem.CartItemResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;
@Getter
@AllArgsConstructor
public class CartItem {

    @NotNull
    private Long cartId;

    @NotNull
    private Set<CartItemResponse> cartItems;

    @PositiveOrZero
    private  double totalAmount;

}
