package controller;

import entity.CommunityHub.Comment;
import service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Create a new comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequest request) {
        try {
            String username = getCurrentUsername();
            Comment comment = commentService.createComment(
                    request.getContent(),
                    request.getPostId(),
                    username,
                    request.getParentCommentId()
            );

            return ResponseEntity.ok(createCommentResponse(comment, "Comment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentService.getCommentsByPost(postId, pageable);
            return ResponseEntity.ok(createPageResponse(comments, "Comments retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get top-level comments for a post (no replies)
    @GetMapping("/post/{postId}/top-level")
    public ResponseEntity<?> getTopLevelCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentService.getTopLevelCommentsByPost(postId, pageable);
            return ResponseEntity.ok(createPageResponse(comments, "Top-level comments retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get replies to a specific comment
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getCommentReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            Page<Comment> replies = commentService.getCommentReplies(commentId, pageable);
            return ResponseEntity.ok(createPageResponse(replies, "Comment replies retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Optional<Comment> comment = commentService.getCommentByIdWithUserAndPost(id);
            if (comment.isPresent()) {
                return ResponseEntity.ok(createCommentResponse(comment.get(), "Comment retrieved successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody UpdateCommentRequest request) {
        try {
            String username = getCurrentUsername();
            Comment comment = commentService.updateComment(id, request.getContent(), username);
            return ResponseEntity.ok(createCommentResponse(comment, "Comment updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            commentService.deleteComment(id, username);
            return ResponseEntity.ok(createSuccessResponse("Comment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get comments by current user
    @GetMapping("/my-comments")
    public ResponseEntity<?> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            String username = getCurrentUsername();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentService.getCommentsByUser(username, pageable);
            return ResponseEntity.ok(createPageResponse(comments, "Your comments retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get recent comments across all posts
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentService.getRecentComments(pageable);
            return ResponseEntity.ok(createPageResponse(comments, "Recent comments retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper method to get current username
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Response helper methods
    private Map<String, Object> createCommentResponse(Comment comment, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", comment);
        return response;
    }

    private Map<String, Object> createPageResponse(Page<Comment> comments, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", comments.getContent());
        response.put("currentPage", comments.getNumber());
        response.put("totalItems", comments.getTotalElements());
        response.put("totalPages", comments.getTotalPages());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    // Request DTO classes
    public static class CreateCommentRequest {
        private String content;
        private Long postId;
        private Long parentCommentId;

        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getParentCommentId() { return parentCommentId; }
        public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }
    }

    public static class UpdateCommentRequest {
        private String content;

        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}