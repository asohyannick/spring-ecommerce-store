package com.mercado.mercadoSpring.controller.product;
import com.mercado.mercadoSpring.config.ApiResponseConfig;
import com.mercado.mercadoSpring.dto.product.PageResponse;
import com.mercado.mercadoSpring.dto.product.ProductDto;
import com.mercado.mercadoSpring.dto.product.ProductResponseDTO;
import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.mappers.product.ProductMapper;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import com.mercado.mercadoSpring.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/${API_VERSION}/products")
@Tag(name= "Products Management Endpoints")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Create a new product
    @PostMapping("/create-product")
    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    public ResponseEntity<ApiResponseConfig<ProductDto>> createProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        ProductDto savedProductDto = productMapper.toDto(savedProduct);
        ApiResponseConfig<ProductDto> response = new ApiResponseConfig<>(
                "🎉 Product created successfully",
                savedProductDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-products")
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    public ResponseEntity<ApiResponseConfig<List<ProductDto>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();

        ApiResponseConfig<List<ProductDto>> response = new ApiResponseConfig<>(
                "Fetched all products successfully 🎉",
                productDtos
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/single-product/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    public ResponseEntity<ApiResponseConfig<ProductDto>> getProductById(
            @PathVariable Long id
    ) {
        Product product = productService.getProductById(id);
        ProductDto productDto = productMapper.toDto(product);

        ApiResponseConfig<ProductDto> response = new ApiResponseConfig<>(
                "Product retrieved successfully ✅",
                productDto
        );
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-product/{id}")
    @Operation(summary = "Update product", description = "Update an existing product by its ID")
    public ResponseEntity<ApiResponseConfig<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto
    ) {
        Product product = productMapper.toEntity(productDto);
        product.setId(id);

        Product updatedProduct = productService.updateProduct(id, product);
        ProductDto updatedProductDto = productMapper.toDto(updatedProduct);

        ApiResponseConfig<ProductDto> response = new ApiResponseConfig<>(
                "Product updated successfully 🔄",
                updatedProductDto
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-product/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    public ResponseEntity<ApiResponseConfig<ProductDto>> deleteProduct(
            @PathVariable Long id
    ) {
        // 1️⃣ Fetch product before deletion
        Product product = productService.getProductById(id);
        ProductDto deletedProductDto = productMapper.toDto(product);

        // 2️⃣ Delete the product
        productService.deleteProduct(id);

        // 3️⃣ Return deleted product with a success message
        ApiResponseConfig<ProductDto> response = new ApiResponseConfig<>(
                "Product has been deleted successfully! 🗑️",
                deletedProductDto
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-products")
    @Operation(summary = "Search products", description = "Search for products based on various criteria")
    public ResponseEntity<ApiResponseConfig<List<ProductDto>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        List<Product> products = productService.searchProducts(
                keyword, brand, category, minPrice, maxPrice, available, minRating, maxRating
        );

        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();

        ApiResponseConfig<List<ProductDto>> response = new ApiResponseConfig<>(
                "Product search completed successfully 🔍",
                productDtos
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/available/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves products belonging to a specific category")
    public ResponseEntity<ApiResponseConfig<List<ProductDto>>> getAllProductsByCategory(
            @PathVariable String category
    ) {
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();

        ApiResponseConfig<List<ProductDto>> response = new ApiResponseConfig<>(
                "Products fetched successfully by category 📦",
                productDtos
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/available-by-description/{description}")
    @Operation(summary = "Get products by description", description = "Retrieves products matching a specific description")
    public ResponseEntity<ApiResponseConfig<List<ProductDto>>> getProductsByDescription(
            @PathVariable String description
    ) {
        List<Product> products = productService.findProductsByDescription(description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();
        ApiResponseConfig<List<ProductDto>> response = new ApiResponseConfig<>(
                "Products fetched by description successfully 📝",
                productDtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Get products by category and description", description = "Retrieves products matching both category and description")
    public ResponseEntity<ApiResponseConfig<List<ProductDto>>> getProductsByCategoryAndDescription(
            @RequestParam String category,
            @RequestParam String description
    ) {
        List<Product> products = productService.getProductsByCategoryAndDescription(category, description);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .toList();

        ApiResponseConfig<List<ProductDto>> response = new ApiResponseConfig<>(
                "Products have been fetched by both category and description successfully! 🎯",
                productDtos
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Filter, sort and paginate products",
            description = "Allows users to filter products by keyword, brand, category, pricing, availability, rating, and sort by multiple fields."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/filter")
    public ApiResponseConfig<PageResponse<ProductResponseDTO>> filterProducts(
            @Parameter(description = "Search keyword in name or description") @RequestParam(required = false) String keyword,
            @Parameter(description = "Brand of the product") @RequestParam(required = false) String brand,
            @Parameter(description = "Category of the product") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Availability status") @RequestParam(required = false) Boolean available,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) Double minRating,
            @Parameter(description = "Maximum rating") @RequestParam(required = false) Double maxRating,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Fields to sort by, comma-separated") @RequestParam(defaultValue = "price") List<String> sortFields,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String direction
    ) {
        PageResponse<ProductResponseDTO> products = productService.filterProducts(
                keyword, brand, category, minPrice, maxPrice, available,
                minRating, maxRating, page, size, sortFields, direction
        );
        return new ApiResponseConfig<>(
                "Products filtered, sorted, and paginated successfully 🛒",
                products
        );
    }
}