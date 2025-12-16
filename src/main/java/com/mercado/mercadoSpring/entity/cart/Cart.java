package com.mercado.mercadoSpring.entity.cart;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
@Entity(name = "cart")
@Data
@Getter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private BigDecimal price;
    private String description;
    private Long productId;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
}
