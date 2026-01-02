package com.mercado.mercadoSpring.entity.cartItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mercado.mercadoSpring.entity.cart.Cart;
import com.mercado.mercadoSpring.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
        },
        indexes = {
                @Index(name = "idx_cart_items_cart_id", columnList = "cart_id"),
                @Index(name = "idx_cart_items_product_id", columnList = "product_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Helps prevent lost updates during concurrent cart modifications
    @Version
    private Long version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    public CartItem(Cart cart, Product product, int quantity, BigDecimal unitPrice) {
        this.cart = Objects.requireNonNull(cart, "cart must not be null");
        this.product = Objects.requireNonNull(product, "product must not be null");
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("unitPrice must be >= 0");
        }
        this.unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}


