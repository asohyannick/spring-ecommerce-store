package com.mercado.mercadoSpring.service.cart;
import com.mercado.mercadoSpring.entity.cart.Cart;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import com.mercado.mercadoSpring.repository.cart.CartRepository;
import com.mercado.mercadoSpring.repository.cartItem.CartItemRepository;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartItem addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .map(cartItem -> {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    return cartItemRepository.save(cartItem);
                })
                .orElseGet(() -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setProduct(productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found")));
                    cartItem.setQuantity(quantity);
                    return cartItemRepository.save(cartItem);
                });

    }

    public void removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteAll(cart.getCartItems());
    }

    public ArrayList<CartItem> getCartById(Long cartId) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));
            cart.getCartItems().size();
        return new ArrayList<>(cart.getCartItems());
    }

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

   public List<CartItem> updateItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        return Collections.singletonList(cartItemRepository.save(cartItem));
    }

    public List<CartItem> getProductDetailsInCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return new ArrayList<>(cart.getCartItems());
    }

    public int calculateTotalQuantity(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double calculateTotalCartPrice(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getCartItems().stream()
                .mapToDouble(item -> item.getUnitPrice().doubleValue() * item.getQuantity())
                .sum();
    }
}
