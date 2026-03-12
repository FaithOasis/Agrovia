package entity;

public enum NotificationType {
    // Existing values...
    POST_LIKE,
    POST_COMMENT,
    COMMENT_REPLY,
    USER_MENTION,
    SYSTEM_ANNOUNCEMENT,
    WELCOME_MESSAGE,
    NEGOTIATION,
    ORDER,
    PAYMENT,
    SHIPPING,

    // Add these new values for Resource Sharing
    RESOURCE_LISTED,
    BOOKING_REQUEST,
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    BOOKING_REMINDER,
    BOOKING_COMPLETED,
    REVIEW_RECEIVED,
    PAYMENT_RECEIVED,
    DEPOSIT_RETURNED
}