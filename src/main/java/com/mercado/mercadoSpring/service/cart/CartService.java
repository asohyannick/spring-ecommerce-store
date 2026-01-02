package com.mercado.mercadoSpring.service.cart;
import com.mercado.mercadoSpring.dto.cart.CartResponse;
import com.mercado.mercadoSpring.dto.cartItem.CartItemResponse;
import com.mercado.mercadoSpring.entity.cart.Cart;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.repository.cart.CartRepository;
import com.mercado.mercadoSpring.repository.cartItem.CartItemRepository;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
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

    @Transactional
    public Cart createCart() {
        Cart cart = new Cart();
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(cart -> {
                    Set<CartItemResponse> items = cart.getCartItems().stream()
                            .map(this::toItemResponse)
                            .collect(Collectors.toSet());
                    return new CartResponse(
                            cart.getId(),
                            items,
                            cart.getTotalItems(),
                            cart.getTotal()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CartResponse getCartResponse(Long cartId) {
        Cart cart = getCartOrThrow(cartId);

        Set<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toSet());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        return new CartResponse(cart.getId(), items, totalItems, totalAmount);
    }

    @Transactional
    public CartResponse addItemToCart(
            Long cartId,
            Long productId,
            int quantityToAdd
    ) {
        if (quantityToAdd <= 0) throw new IllegalArgumentException("quantity must be > 0");
        Cart cart = getCartOrThrow(cartId);
        Product product = getProductOrThrow(productId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElse(null);
        if (item == null) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
            newItem.setQuantity(quantityToAdd);
            cartItemRepository.save(newItem);
        } else {
            item.setQuantity(item.getQuantity() + quantityToAdd);
            cartItemRepository.save(item);
        }
        return getCartResponse(cartId);
    }

    @Transactional
    public CartResponse updateProductQuantity(
            Long cartId,
            Long productId,
            int quantity
    ) {
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");

        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found for cartId=" + cartId + ", productId=" + productId
                ));

        if (quantity == 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCartResponse(cartId);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = getCartOrThrow(cartId);
        cart.getCartItems().clear();   // orphanRemoval handles deletes
        cartRepository.save(cart);
    }

    @Transactional
    public CartResponse removeProductFromCart(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found for cartId=" + cartId + ", productId=" + productId
                ));
        cartItemRepository.delete(item);
        return getCartResponse(cartId);
    }

    private Cart getCartOrThrow(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found: " + cartId));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
