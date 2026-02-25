package repository;

import entity.MarketPlace.Product;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by seller
    Page<Product> findBySeller(User seller, Pageable pageable);

    // Find products by status
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

    // Search products with multiple filters
    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:searchTerm IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("status") Product.ProductStatus status,
                                 @Param("searchTerm") String searchTerm,
                                 Pageable pageable);

    // Find products by seller and status
    List<Product> findBySellerAndStatus(User seller, Product.ProductStatus status);

    // Find all negotiable products that are available
    @Query("SELECT p FROM Product p WHERE p.isNegotiable = true AND p.status = 'AVAILABLE'")
    List<Product> findNegotiableProducts();

    // Count products by seller
    Long countBySeller(User seller);

    // Find products by category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Find products by price range
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}