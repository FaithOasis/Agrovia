package com.agrosolutions.AgroVia.service;

import com.agrosolutions.AgroVia.entity.Comment;
import com.agrosolutions.AgroVia.entity.Post;
import com.agrosolutions.AgroVia.entity.User;
import com.agrosolutions.AgroVia.repository.CommentRepository;
import com.agrosolutions.AgroVia.repository.PostRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Create a new comment
    public Comment createComment(String content, Long postId, String username, Long parentCommentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment;

        if (parentCommentId != null) {
            // This is a reply to an existing comment
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found with id: " + parentCommentId));
            comment = new Comment(content, user, post, parentComment);

            // Create notification for comment reply
            notificationService.createCommentReplyNotification(user, parentComment, comment);
        } else {
            // This is a top-level comment
            comment = new Comment(content, user, post);

            // Create notification for post comment (only if not commenting on own post)
            if (!user.getId().equals(post.getUser().getId())) {
                notificationService.createPostCommentNotification(user, post, comment);
            }
        }

        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }

    // Get all comments for a post
    public Page<Comment> getCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
    }

    // Get top-level comments for a post (no replies)
    public Page<Comment> getTopLevelCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return commentRepository.findByPostAndParentCommentIsNullOrderByCreatedAtDesc(post, pageable);
    }

    // Get replies to a specific comment
    public Page<Comment> getCommentReplies(Long commentId, Pageable pageable) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        return commentRepository.findByParentCommentOrderByCreatedAtDesc(parentComment, pageable);
    }

    // Get comment by ID
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    // Get comment with user and post details
    public Optional<Comment> getCommentByIdWithUserAndPost(Long id) {
        return commentRepository.findByIdWithUserAndPost(id);
    }

    // Update comment
    public Comment updateComment(Long commentId, String content, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        // Check if the user owns the comment
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(content);
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // Delete comment
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        // Check if the user owns the comment
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        // Cleanup notifications before deleting comment
        notificationService.cleanupCommentNotifications(commentId);

        commentRepository.delete(comment);
    }

    // Get comments by user
    public Page<Comment> getCommentsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return commentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    // Get recent comments across all posts
    public Page<Comment> getRecentComments(Pageable pageable) {
        return commentRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // Count comments for a post
    public long countCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return commentRepository.countByPost(post);
    }
}