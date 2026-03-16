package com.agrosolutions.AgroVia.repository;

import com.agrosolutions.AgroVia.entity.Post;
import com.agrosolutions.AgroVia.entity.PostCategory;
import com.agrosolutions.AgroVia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find all posts by a specific user
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Find all posts by category
    Page<Post> findByCategoryOrderByCreatedAtDesc(PostCategory category, Pageable pageable);

    // Find all published posts with pagination
    Page<Post> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    // Search posts by title or content
    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> searchPublishedPosts(@Param("query") String query, Pageable pageable);

    // Find posts with most comments (popular posts)
    @Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY SIZE(p.comments) DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    // Count posts by user
    long countByUser(User user);

    // Find post by ID with user eager loaded
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :id AND p.isPublished = true")
    Optional<Post> findByIdWithUser(@Param("id") Long id);
}