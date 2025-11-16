package com.mercado.mercadoSpring.service.product;
import com.mercado.mercadoSpring.dto.product.PageResponse;
import com.mercado.mercadoSpring.dto.product.ProductResponseDTO;
import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.exception.ResourceNotFoundException;
import com.mercado.mercadoSpring.mappers.product.ProductMapper;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Create product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    // Update product
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        // Update fields
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setAvailable(updatedProduct.getAvailable());
        existingProduct.setRating(updatedProduct.getRating());
        existingProduct.setNumberOfReviews(updatedProduct.getNumberOfReviews());
        existingProduct.setCountryOfOrigin(updatedProduct.getCountryOfOrigin());
        existingProduct.setCity(updatedProduct.getCity());
        existingProduct.setState(updatedProduct.getState());
        existingProduct.setZipCode(updatedProduct.getZipCode());
        existingProduct.setImageURL(updatedProduct.getImageURL());

        return productRepository.save(existingProduct);
    }

    // Delete product
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    // Filter/query methods
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product>  findProductsByDescription(String description) {
        return productRepository.findByDescriptionContaining(description);
    }

    public List<Product> getProductsByCategoryAndDescription(String category, String description) {
        if (category != null) category = category.trim();
        if (description != null) description = description.trim();
        return productRepository.findByCategoryAndDescription(category, description);
    }

    public List<Product> searchProducts(
            String keyword,
            String brand,
            String category,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            Double minRating,
            Double maxRating
    ) {
        return productRepository.searchProducts(
                keyword,
                brand,
                category,
                minPrice,
                maxPrice,
                available,
                minRating,
                maxRating
        );
    }

    @Cacheable(
            value = "filteredProducts",
            key = "T(String).format('%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s', " +
                    "#keyword, #brand, #category, #minPrice, #maxPrice, #available, " +
                    "#minRating, #maxRating, #page, #size, #direction, T(java.lang.String).join(',', #sortFields))"
    )
    public PageResponse<ProductResponseDTO> filterProducts(
            String keyword,
            String brand,
            String category,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            Double minRating,
            Double maxRating,
            int page,
            int size,
            List<String> sortFields,
            String direction
    ) {

        // Multi-field dynamic sorting
        Sort sort = Sort.by(
                sortFields.stream()
                        .map(f -> direction.equalsIgnoreCase("desc") ? Sort.Order.desc(f) : Sort.Order.asc(f))
                        .toList()
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponseDTO> productPage = productRepository.filterProducts(
                keyword, brand, category, minPrice, maxPrice, available, minRating, maxRating, pageable
        ).map(productMapper::toResponseDTO);

        return new PageResponse<>(
                productPage.getContent(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }
}

