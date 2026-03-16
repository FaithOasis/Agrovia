package com.agrosolutions.AgroVia.repository;
import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceImageRepository extends JpaRepository<ResourceImage, Long> {
    // Find all images for a resource
    List<ResourceImage> findByResourceOrderBySortOrderAsc(Resource resource);

    // Find primary image for a resource
    Optional<ResourceImage> findByResourceAndIsPrimaryTrue(Resource resource);

    // Delete all images for a resource
    @Modifying
    @Query("DELETE FROM ResourceImage i WHERE i.resource = :resource")
    void deleteByResource(@Param("resource") Resource resource);

    // Set image as primary (and unset others)
    @Modifying
    @Query("UPDATE ResourceImage i SET i.isPrimary = false WHERE i.resource = :resource")
    void resetPrimaryForResource(@Param("resource") Resource resource);

    @Modifying
    @Query("UPDATE ResourceImage i SET i.isPrimary = true WHERE i.id = :imageId AND i.resource = :resource")
    void setAsPrimary(@Param("imageId") Long imageId, @Param("resource") Resource resource);

    // Count images for a resource
    long countByResource(Resource resource);
}
