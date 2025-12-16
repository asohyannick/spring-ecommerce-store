package com.mercado.mercadoSpring.entity.cartItem;
import com.mercado.mercadoSpring.entity.cart.Cart;
import com.mercado.mercadoSpring.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    public CartItem(Cart cart, Product product, int quantity, BigDecimal unitPrice) {
        this.cart = cart;
        this.product = product;
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    // --- Custom Setters with Validation ---
    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() < 0)
            throw new IllegalArgumentException("unitPrice must be >= 0");
        this.unitPrice = unitPrice;
    }

    @Transient
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

