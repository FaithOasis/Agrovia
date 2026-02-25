package repository;

import entity.CommunityHub.Comment;
import entity.CommunityHub.Post;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a specific post
    Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);

    // Find all comments by a specific user
    Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Find top-level comments (no parent) for a post
    Page<Comment> findByPostAndParentCommentIsNullOrderByCreatedAtDesc(Post post, Pageable pageable);

    // Find replies to a specific comment
    Page<Comment> findByParentCommentOrderByCreatedAtDesc(Comment parentComment, Pageable pageable);

    // Count comments by post
    long countByPost(Post post);

    // Count comments by user
    long countByUser(User user);

    // Find comment with user and post details loaded
    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.post WHERE c.id = :id")
    Optional<Comment> findByIdWithUserAndPost(@Param("id") Long id);

    // Find recent comments across all posts
    Page<Comment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
