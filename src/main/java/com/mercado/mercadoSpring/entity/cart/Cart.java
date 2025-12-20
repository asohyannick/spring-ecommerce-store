package com.mercado.mercadoSpring.entity.cart;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_carts_id", columnList = "id")
})
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    /** Convenience methods to keep both sides in sync */
    public void addItem(CartItem item) {
        Objects.requireNonNull(item, "item must not be null");
        item.setCart(this);
        cartItems.add(item);
    }

    public void removeItem(CartItem item) {
        if (item == null) return;
        cartItems.remove(item);
        item.setCart(null);
    }

    @Transient
    public BigDecimal getTotal() {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

