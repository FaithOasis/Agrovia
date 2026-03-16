package com.agrosolutions.AgroVia.repository;

import com.agrosolutions.AgroVia.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long>{
    // Find category by name (exact match)
    Optional<ResourceCategory> findByName(String name);

    // Check if category exists by name
    boolean existsByName(String name);

    // Find all categories ordered by name
    List<ResourceCategory> findAllByOrderByNameAsc();

    // Find categories with resource count
    @Query("SELECT c, COUNT(r) FROM ResourceCategory c LEFT JOIN c.resources r GROUP BY c ORDER BY c.name ASC")
    List<Object[]> findAllWithResourceCount();

    // Find categories that have at least one available resource
    @Query("SELECT DISTINCT c FROM ResourceCategory c JOIN c.resources r WHERE r.status = 'AVAILABLE'")
    List<ResourceCategory> findCategoriesWithAvailableResources();
}
