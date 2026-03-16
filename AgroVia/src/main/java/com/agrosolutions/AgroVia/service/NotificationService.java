package com.agrosolutions.AgroVia.service;

import com.agrosolutions.AgroVia.entity.Comment;
import com.agrosolutions.AgroVia.entity.Post;
import com.agrosolutions.AgroVia.repository.NotificationRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agrosolutions.AgroVia.entity.Notification;
import com.agrosolutions.AgroVia.entity.NotificationType;
import com.agrosolutions.AgroVia.entity.User;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // WebSocket will be added later - using commented version for now
    // @Autowired
    // private SimpMessagingTemplate messagingTemplate;

    // Create a post like notification
    public void createPostLikeNotification(User liker, Post post) {
        if (!liker.getId().equals(post.getUser().getId())) { // Don't notify yourself
            String message = liker.getUsername() + " liked your post: " + post.getTitle();
            Notification notification = new Notification(
                    message,
                    NotificationType.POST_LIKE,
                    post.getUser(),
                    liker,
                    post.getId()
            );
            notificationRepository.save(notification);
            // sendRealTimeNotification(post.getUser().getUsername(), notification);
        }
    }

    // Create a post comment notification
    public void createPostCommentNotification(User commenter, Post post, Comment comment) {
        if (!commenter.getId().equals(post.getUser().getId())) { // Don't notify yourself
            String message = commenter.getUsername() + " commented on your post: " + post.getTitle();
            Notification notification = new Notification(
                    message,
                    NotificationType.POST_COMMENT,
                    post.getUser(),
                    commenter,
                    post.getId(),
                    comment.getId()
            );
            notificationRepository.save(notification);
            // sendRealTimeNotification(post.getUser().getUsername(), notification);
        }
    }

    // Create a comment reply notification
    public void createCommentReplyNotification(User replier, Comment parentComment, Comment reply) {
        if (!replier.getId().equals(parentComment.getUser().getId())) { // Don't notify yourself
            String message = replier.getUsername() + " replied to your comment";
            Notification notification = new Notification(
                    message,
                    NotificationType.COMMENT_REPLY,
                    parentComment.getUser(),
                    replier,
                    parentComment.getPost().getId(),
                    parentComment.getId()
            );
            notificationRepository.save(notification);
            // sendRealTimeNotification(parentComment.getUser().getUsername(), notification);
        }
    }

    // Create a user mention notification
    public void createUserMentionNotification(User mentioner, User mentionedUser, Post post, Comment comment) {
        String message = mentioner.getUsername() + " mentioned you in a comment";
        Notification notification = new Notification(
                message,
                NotificationType.USER_MENTION,
                mentionedUser,
                mentioner,
                post.getId(),
                comment.getId()
        );
        notificationRepository.save(notification);
        // sendRealTimeNotification(mentionedUser.getUsername(), notification);
    }

    // Create a system announcement - ADDED THIS METHOD
    public void createSystemAnnouncement(String message, User recipient) {
        Notification notification = new Notification(
                message,
                NotificationType.SYSTEM_ANNOUNCEMENT,
                recipient
        );
        notificationRepository.save(notification);
        // sendRealTimeNotification(recipient.getUsername(), notification);
    }

    // Create welcome message for new user
    public void createWelcomeNotification(User newUser) {
        String message = "Welcome to AgroVia! Start by exploring posts and connecting with other farmers.";
        Notification notification = new Notification(
                message,
                NotificationType.WELCOME_MESSAGE,
                newUser
        );
        notificationRepository.save(notification);
        // sendRealTimeNotification(newUser.getUsername(), notification);
    }

    // Get all notifications for a user
    public Page<Notification> getUserNotifications(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
    }

    // Get unread notifications for a user
    public Page<Notification> getUnreadNotifications(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user, pageable);
    }

    // Get unread notification count
    public long getUnreadNotificationCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    // Mark all notifications as read
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        notificationRepository.markAllAsRead(user);
    }

    // Mark specific notification as read
    public void markAsRead(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        notificationRepository.markAsRead(notificationId, user);
    }

    // Delete notification
    public void deleteNotification(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        // Check if the user owns the notification
        if (!notification.getRecipient().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }

    // Cleanup notifications when post is deleted
    public void cleanupPostNotifications(Long postId) {
        List<Notification> notifications = notificationRepository.findByPostId(postId);
        notificationRepository.deleteAll(notifications);
    }

    // Cleanup notifications when comment is deleted
    public void cleanupCommentNotifications(Long commentId) {
        List<Notification> notifications = notificationRepository.findByCommentId(commentId);
        notificationRepository.deleteAll(notifications);
    }


    // Real-time notification will be implemented later with WebSocket
    /*
    private void sendRealTimeNotification(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/notifications",
            notification
        );
    }
    */
}