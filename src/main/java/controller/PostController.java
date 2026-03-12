package controller;

import entity.Post;
import entity.PostCategory;
import service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Create a new post
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        try {
            String username = getCurrentUsername();
            Post post = postService.createPost(
                    request.getTitle(),
                    request.getContent(),
                    request.getCategory(),
                    request.getImageUrl(),
                    username
            );

            return ResponseEntity.ok(createPostResponse(post, "Post created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get all published posts with pagination
    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Post> posts = postService.getAllPublishedPosts(pageable);
            return ResponseEntity.ok(createPageResponse(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Optional<Post> post = postService.getPostByIdWithUser(id);
            if (post.isPresent()) {
                return ResponseEntity.ok(createPostResponse(post.get(), "Post retrieved successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Update post
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody UpdatePostRequest request) {
        try {
            String username = getCurrentUsername();
            Post post = postService.updatePost(
                    id,
                    request.getTitle(),
                    request.getContent(),
                    request.getCategory(),
                    request.getImageUrl(),
                    username
            );

            return ResponseEntity.ok(createPostResponse(post, "Post updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            postService.deletePost(id, username);
            return ResponseEntity.ok(createSuccessResponse("Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get posts by category
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getPostsByCategory(
            @PathVariable PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Post> posts = postService.getPostsByCategory(category, pageable);
            return ResponseEntity.ok(createPageResponse(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get posts by current user
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            String username = getCurrentUsername();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Post> posts = postService.getPostsByUser(username, pageable);
            return ResponseEntity.ok(createPageResponse(posts, "Your posts retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Search posts
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Post> posts = postService.searchPosts(query, pageable);
            return ResponseEntity.ok(createPageResponse(posts, "Search results retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Like a post
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            Post post = postService.likePost(id, username);
            return ResponseEntity.ok(createPostResponse(post, "Post liked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Unlike a post
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            Post post = postService.unlikePost(id, username);
            return ResponseEntity.ok(createPostResponse(post, "Post unliked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Toggle publish status
    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> togglePublishStatus(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            Post post = postService.togglePublishStatus(id, username);
            String message = post.isPublished() ? "Post published successfully" : "Post unpublished successfully";
            return ResponseEntity.ok(createPostResponse(post, message));
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
    private Map<String, Object> createPostResponse(Post post, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", post);
        return response;
    }

    private Map<String, Object> createPageResponse(Page<Post> posts, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
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
    public static class CreatePostRequest {
        private String title;
        private String content;
        private PostCategory category;
        private String imageUrl;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class UpdatePostRequest {
        private String title;
        private String content;
        private PostCategory category;
        private String imageUrl;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
