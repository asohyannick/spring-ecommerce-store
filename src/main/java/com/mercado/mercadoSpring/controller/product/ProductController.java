package com.mercado.mercadoSpring.controller.product;
import com.mercado.mercadoSpring.dto.product.ProductDto;
import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.mappers.product.ProductMapper;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import com.mercado.mercadoSpring.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/${spring.application.APP_VERSION}/product")
@RequiredArgsConstructor
@Tag(name = "Product Management Endpoints", description = "APIs for managing products")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Create a new product
    @PostMapping("/create-product")
    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        ProductDto savedProductDto = productMapper.toDto(savedProduct);
        return ResponseEntity.ok(savedProductDto);
    }

    @GetMapping("/all-products")
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/single-product/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDto productDto = productMapper.toDto(product);
        return ResponseEntity.ok(productDto);
    }

    @PutMapping("/update-product/{id}")
    @Operation(summary = "Update product", description = "Update an existing product by its ID")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setId(id);
        Product updatedProduct = productService.updateProduct(id ,product);
        ProductDto updatedProductDto = productMapper.toDto(updatedProduct);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/delete-product/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/search-products")
    @Operation(summary = "Search products", description = "Search for products based on various criteria")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        List<Product> products = productService.searchProducts(
                keyword, brand, category, minPrice, maxPrice, isAvailable, minRating, maxRating
        );

        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();

        return ResponseEntity.ok(productDtos);
    }


    @GetMapping("/available/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves products belonging to a specific category")
    public ResponseEntity<List<ProductDto>> getAllProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/available/{description}")
    @Operation(summary = "Get products by description", description = "Retrieves products matching a specific description")
    public ResponseEntity<List<ProductDto>> getProductsByDescription(@PathVariable String description) {
        List<Product> products = productService.findProductsByDescription(description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/search")
    @Operation(summary = "Get products by category and description", description = "Retrieves products matching both category and description")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryAndDescription(
            @RequestParam String category,
            @RequestParam String description) {
        List<Product> products = productService.getProductsByCategoryAndDescription(category, description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

}