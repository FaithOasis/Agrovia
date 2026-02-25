package entity.CommunityHub;

public enum NotificationType {
    POST_LIKE,              // Someone liked your post
    POST_COMMENT,           // Someone commented on your post
    COMMENT_REPLY,          // Someone replied to your comment
    USER_MENTION,           // Someone mentioned you in a comment
    SYSTEM_ANNOUNCEMENT,    // System-wide announcement
    WELCOME_MESSAGE,        // Welcome message for new users
    NEGOTIATION,            // New negotiation or negotiation update
    ORDER,                  // Order status update
    PAYMENT,                // Payment related notification
    SHIPPING                // Shipping status update
}