package service;

import entity.CommunityHub.Post;
import entity.CommunityHub.PostCategory;
import entity.User;
import repository.PostRepository;
import repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Create a new post
    public Post createPost(String title, String content, PostCategory category, String imageUrl, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Post post = new Post(title, content, category, user);
        post.setImageUrl(imageUrl);

        return postRepository.save(post);
    }

    // Get all published posts with pagination
    public Page<Post> getAllPublishedPosts(Pageable pageable) {
        return postRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
    }

    // Get post by ID
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // Get post by ID with user details
    public Optional<Post> getPostByIdWithUser(Long id) {
        return postRepository.findByIdWithUser(id);
    }

    // Update post
    public Post updatePost(Long postId, String title, String content, PostCategory category, String imageUrl, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if the user owns the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own posts");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setImageUrl(imageUrl);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // Delete post
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if the user owns the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        // Cleanup notifications before deleting post
        notificationService.cleanupPostNotifications(postId);

        postRepository.delete(post);
    }

    // Get posts by category
    public Page<Post> getPostsByCategory(PostCategory category, Pageable pageable) {
        return postRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
    }

    // Get posts by user
    public Page<Post> getPostsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    // Search posts
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.searchPublishedPosts(query, pageable);
    }

    // Get popular posts
    public Page<Post> getPopularPosts(Pageable pageable) {
        return postRepository.findPopularPosts(pageable);
    }

    // Like a post
    public Post likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        post.likePost(user);
        Post savedPost = postRepository.save(post);

        // Create notification for post owner
        notificationService.createPostLikeNotification(user, post);

        return savedPost;
    }

    // Unlike a post
    public Post unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        post.unlikePost(user);
        return postRepository.save(post);
        // No notification for unliking
    }

    // Toggle post publish status
    public Post togglePublishStatus(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if the user owns the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own posts");
        }

        post.setPublished(!post.isPublished());
        return postRepository.save(post);
    }
}