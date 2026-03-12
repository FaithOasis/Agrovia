package repository;

import entity.Negotiation;
import entity.Product;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, Long> {

    // Find negotiations by buyer (the person making the offer)
    Page<Negotiation> findByBuyer(User buyer, Pageable pageable);

    // Find negotiations by seller (through the product)
    @Query("SELECT n FROM Negotiation n WHERE n.product.seller = :seller")
    Page<Negotiation> findBySeller(@Param("seller") User seller, Pageable pageable);

    // Find negotiations by product
    List<Negotiation> findByProduct(Product product);

    // Find negotiations by product and status
    List<Negotiation> findByProductAndStatus(Product product, Negotiation.NegotiationStatus status);

    // Find negotiations by buyer and status
    List<Negotiation> findByBuyerAndStatus(User buyer, Negotiation.NegotiationStatus status);

    // Check if a pending negotiation exists between buyer and product
    boolean existsByProductAndBuyerAndStatus(Product product, User buyer, Negotiation.NegotiationStatus status);

    // Find all pending negotiations for a seller
    @Query("SELECT n FROM Negotiation n WHERE n.product.seller = :seller AND n.status = 'PENDING'")
    List<Negotiation> findPendingNegotiationsForSeller(@Param("seller") User seller);

    // Count negotiations by status for a product
    Long countByProductAndStatus(Product product, Negotiation.NegotiationStatus status);

    // Find negotiations by status
    Page<Negotiation> findByStatus(Negotiation.NegotiationStatus status, Pageable pageable);

    // Find negotiation by product and buyer (if exists)
    Optional<Negotiation> findByProductAndBuyer(Product product, User buyer);
}