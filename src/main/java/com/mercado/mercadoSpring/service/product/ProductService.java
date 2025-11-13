package com.mercado.mercadoSpring.service.product;

import com.mercado.mercadoSpring.entity.product.Product;
import com.mercado.mercadoSpring.exception.ResourceNotFoundException;
import com.mercado.mercadoSpring.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
        existingProduct.setIsAvailable(updatedProduct.getIsAvailable());
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
            Boolean isAvailable,
            Double minRating,
            Double maxRating
    ) {
        return productRepository.searchProducts(
                keyword,
                brand,
                category,
                minPrice,
                maxPrice,
                isAvailable,
                minRating,
                maxRating
        );
    }

}

