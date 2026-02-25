package service;

import com.agrosolutions.AgroVia.dto.*;
import entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import entity.CommunityHub.PostCategory;
import entity.MarketPlace.Product;
import repository.PostCategoryRepository;
import repository.ProductRepository;
import repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostCategoryRepository categoryRepository;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Create new product
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User currentUser = getCurrentUser();

        // Validate request
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be greater than zero");
        }

        if (request.getQuantityAvailable() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantityAvailable(request.getQuantityAvailable());
        product.setUnit(request.getUnit());
        product.setIsNegotiable(request.getIsNegotiable() != null ? request.getIsNegotiable() : false);
        product.setImageUrl(request.getImageUrl());
        product.setSeller(currentUser);
        product.setStatus(Product.ProductStatus.AVAILABLE);

        // Set category if provided - Since PostCategory is an enum, we need to convert the ID to enum value
        if (request.getCategoryId() != null) {
            // You need to map categoryId to PostCategory enum
            // This depends on how you store categories in your database
            // For now, we'll leave it commented until we see your Product entity
            // product.setCategory(mapToPostCategory(request.getCategoryId()));
        }

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    // Get all available products with pagination
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByStatus(Product.ProductStatus.AVAILABLE, pageable)
                .map(this::mapToProductResponse);
    }

    // Get product by ID
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    // Update product
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own products");
        }

        // Update fields
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantityAvailable(request.getQuantityAvailable());
        product.setUnit(request.getUnit());
        product.setIsNegotiable(request.getIsNegotiable());
        product.setImageUrl(request.getImageUrl());

        // Update category if provided
        if (request.getCategoryId() != null) {
            // product.setCategory(mapToPostCategory(request.getCategoryId()));
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    // Delete product
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    // Update product status
    @Transactional
    public ProductResponse updateProductStatus(Long id, Product.ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own products");
        }

        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    // Get current user's products
    public Page<ProductResponse> getMyProducts(Pageable pageable) {
        User currentUser = getCurrentUser();
        return productRepository.findBySeller(currentUser, pageable)
                .map(this::mapToProductResponse);
    }

    // Search products with filters
    public Page<ProductResponse> searchProducts(Long categoryId, BigDecimal minPrice,
                                                BigDecimal maxPrice, String status,
                                                String searchTerm, Pageable pageable) {
        Product.ProductStatus productStatus = status != null ?
                Product.ProductStatus.valueOf(status) : Product.ProductStatus.AVAILABLE;

        return productRepository.searchProducts(categoryId, minPrice, maxPrice,
                        productStatus, searchTerm, pageable)
                .map(this::mapToProductResponse);
    }

    // Map Product entity to ProductResponse DTO
    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantityAvailable(product.getQuantityAvailable());
        response.setUnit(product.getUnit());
        response.setIsNegotiable(product.getIsNegotiable());
        response.setStatus(product.getStatus().toString());
        response.setImageUrl(product.getImageUrl());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        // Map seller - FIXED: Removed phoneNumber
        if (product.getSeller() != null) {
            UserSummary sellerSummary = new UserSummary(
                    product.getSeller().getId(),
                    product.getSeller().getUsername(),
                    product.getSeller().getEmail(),
                    product.getSeller().getFullName()
            );
            response.setSeller(sellerSummary);
        }

        // Map category - FIXED: For Enum, use ordinal() or name()
        if (product.getCategory() != null) {
            // Since PostCategory is an enum, we need to get the enum value
            // Option 1: Use the enum name as the category name
            String categoryName = product.getCategory().name();

            // Option 2: If you want an ID, use ordinal() but be careful as it can change if enum order changes
            Long categoryId = (long) product.getCategory().ordinal();

            CategorySummary categorySummary = new CategorySummary(
                    categoryId,
                    categoryName
            );
            response.setCategory(categorySummary);
        }

        return response;
    }

    // Helper method to map category ID to PostCategory enum (if needed)
    private PostCategory mapToPostCategory(Long categoryId) {
        if (categoryId == null) return null;

        // This maps based on ordinal position - be careful if enum order changes
        PostCategory[] categories = PostCategory.values();
        if (categoryId >= 0 && categoryId < categories.length) {
            return categories[categoryId.intValue()];
        }
        return null;
    }
}