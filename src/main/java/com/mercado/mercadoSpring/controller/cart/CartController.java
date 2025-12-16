package com.mercado.mercadoSpring.controller.cart;
import com.mercado.mercadoSpring.entity.cartItem.CartItem;
import com.mercado.mercadoSpring.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/${API_VERSION}/cart")
@AllArgsConstructor
@Tag(name = "Shopping Cart Management Endpoints", description = "APIs for managing shopping cart operations")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{cartItemId}/add")
    @ApiResponse(responseCode = "200", description = "Successfully added item to cart")
    @Operation(summary = "Add Item to Cart", description = "Add a specified quantity of a product to the cart.")
    public ResponseEntity<CartItem> addItemToCart(
            @PathVariable  Long cartId,
            @PathVariable  Long productId,
            @PathVariable  int quantity
    ) {
        CartItem cartItem = cartService.addItemToCart(cartId, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all cart items")
    @Operation(summary = "Get All Cart Items", description = "Retrieve a list of all cart items in the system.")
    public ResponseEntity<List<CartItem>> getAllCartItems() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/products/details")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product details in cart")
    @Operation(summary = "Get Product Details in Cart", description = "Retrieve detailed information about products in the specified cart.")
    public ResponseEntity<List<CartItem>> getProductDetailsInCart(
            @PathVariable Long cartId
    ) {
        List<CartItem> cartItems = cartService.getProductDetailsInCart(cartId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/{cartId}/items")
    @Operation(summary = "Get Cart Items by Cart ID", description = "Retrieve all items in a specific cart by its ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart items by cart ID")
    public ResponseEntity<List<CartItem>> getCartById(@PathVariable  Long cartId) {
        List<CartItem> cartItems = cartService.getCartById(cartId);
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/item/{cartItemId}/remove")
    @ApiResponse(responseCode = "204", description = "Successfully removed item from cart")
    @Operation(summary = "Remove Item from Cart", description = "Remove a specific item from the cart by its ID.")
    public ResponseEntity<CartItem> removeItemFromCart(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/item/{cartItemId}/update-quantity/{quantity}")
    @Operation(summary = "Update Item Quantity in Cart", description = "Update the quantity of a specific item in the cart.")
    @ApiResponse(responseCode = "204", description = "Successfully updated item quantity in cart")
    public ResponseEntity<CartItem> updateItemQuantity(
            @PathVariable Long cartItemId,
            @PathVariable int quantity
    ) {
        cartService.updateItemQuantity(cartItemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/clear")
    @ApiResponse(responseCode = "204", description = "Successfully cleared the cart")
    @Operation(summary = "Clear Cart", description = "Remove all items from the specified cart.")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}/total-quantity")
    @ApiResponse(responseCode = "200", description = "Successfully calculated total quantity in cart")
    @Operation(summary = "Calculate Total Quantity in Cart", description = "Calculate the total quantity of items in the specified cart.")
    public ResponseEntity<Integer> calculateTotalQuantity(@PathVariable Long cartId) {
        int totalQuantity = cartService.calculateTotalQuantity(cartId);
        return ResponseEntity.ok(totalQuantity);
    }

    @GetMapping("/{cartId}/total-price")
    @ApiResponse(responseCode = "200", description = "Successfully calculated total price in cart")
    @Operation(summary = "Calculate Total Price in Cart", description = "Calculate the total price of items in the specified cart.")
    public ResponseEntity<Double> calculateTotalCartPrice(@PathVariable Long cartId) {
        double totalPrice = cartService.calculateTotalCartPrice(cartId);
        return ResponseEntity.ok(totalPrice);
    }
}
