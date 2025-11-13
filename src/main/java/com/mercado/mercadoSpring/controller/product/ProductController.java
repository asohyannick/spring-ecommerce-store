package com.mercado.mercadoSpring.controller.product;

import com.mercado.mercadoSpring.dto.product.ProductDto;
import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.mappers.product.ProductMapper;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import com.mercado.mercadoSpring.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Create a new product
    @PostMapping("/create-product")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        ProductDto savedProductDto = productMapper.toDto(savedProduct);
        return ResponseEntity.ok(savedProductDto);
    }

    @GetMapping("/all-products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/single-product/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDto productDto = productMapper.toDto(product);
        return ResponseEntity.ok(productDto);
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setId(id);
        Product updatedProduct = productService.updateProduct(id ,product);
        ProductDto updatedProductDto = productMapper.toDto(updatedProduct);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-products")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/products-by-category/{category}")
    public ResponseEntity<List<ProductDto>> getAllProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/products-by-description")
    public ResponseEntity<List<ProductDto>> getProductsByDescription(@RequestParam String description) {
        List<Product> products = productService.getProductsByDescription(description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/products-by-category-and-description")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryAndDescription(
            @RequestParam String category,
            @RequestParam String description) {
        List<Product> products = productService.getProductsByCategoryAndDescription(category, description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/available-products")
    public ResponseEntity<List<ProductDto>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/available-products-by-category/{category}")
    public ResponseEntity<List<ProductDto>> getAvailableProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getAvailableProductsByCategory(category);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/unavailable-products")
    public ResponseEntity<List<ProductDto>> getUnavailableProducts() {
        List<Product> products = productRepository.findByIsAvailableFalse();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/available-products-general")
    public ResponseEntity<List<ProductDto>> getAvailableProductsByCategory() {
        List<Product> products = productRepository.findByIsAvailableTrue();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }
}