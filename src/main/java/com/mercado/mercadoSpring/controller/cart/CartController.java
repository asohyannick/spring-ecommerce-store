package com.mercado.mercadoSpring.controller.cart;
import com.mercado.mercadoSpring.dto.cart.AddToCartRequest;
import com.mercado.mercadoSpring.dto.cart.CartResponse;
import com.mercado.mercadoSpring.dto.cart.UpdateCartItemQuantityRequest;
import com.mercado.mercadoSpring.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/${API_VERSION}/cart")
@RequiredArgsConstructor
@Tag(name = "ShoppingCart Management Endpoints", description = "APIs for managing shopping cart operations")
public class CartController {
    private final CartService cartService;
    @PostMapping("/create-cart")
    @Operation(summary = "Create a new shopping cart successfully", description = "Creates a new empty shopping cart.")
    @ApiResponse(responseCode = "200", description = "Successfully created cart")
    public ResponseEntity<CartResponse> createCart() {
        CartResponse cartResponse = cartService.getCartResponse(
                cartService.createCart().getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @GetMapping("/all-carts")
    @Operation(
            summary = "Get all carts",
            description = "Retrieve all shopping carts in the system (admin/debug use)."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all carts")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/fetch-cart/{cartId}")
    @Operation(
            summary = "Fetch product item from shopping cart successfully",
            description = "Retrieve a cart with items and totals."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved cart"
    )
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long cartId
    ) {
        return ResponseEntity.ok(
                cartService.getCartResponse(cartId)
        );
    }

    @PostMapping("/add-item/{cartId}/item")
    @Operation(
            summary = "Add a product to the shopping cart successfully",
            description = "Adds a product to the cart or increases quantity if it exists."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully added item"
    )
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Long cartId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        System.out.println("DEBUG AddToCartRequest => " + request);
        return ResponseEntity.ok(
                cartService.addItemToCart(
                        cartId,
                        request.productId(),
                        request.quantity()
                )
        );
    }

    @PatchMapping("/update-cart/{cartId}/item/{productId}")
    @Operation(
            summary = "Update product quantity added to shopping cart successfully",
            description = "Updates quantity for a product. Quantity 0 removes the item."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated quantity"
    )
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateProductQuantity(cartId, productId, request.quantity())
        );
    }

    @DeleteMapping("/remove-item/{cartId}/item/{productId}")
    @Operation(
            summary = "Remove product item added from shopping cart successfully",
            description = "Removes a product from the cart."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully removed item"
    )
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                cartService.removeProductFromCart(
                        cartId,
                        productId
                ));
    }

    @DeleteMapping("/clear-cart/{cartId}")
    @Operation(
            summary = "Clear shopping cart successfully",
            description = "Removes all items from the cart."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Successfully cleared cart"
    )
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
