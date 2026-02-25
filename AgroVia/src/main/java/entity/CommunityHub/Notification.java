package entity.CommunityHub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    // Many-to-One relationship with recipient User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    @JsonIgnore
    private User recipient;

    // Many-to-One relationship with sender User (optional - for system notifications)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    @JsonIgnore
    private User sender;

    // References to related entities (optional)
    private Long postId;
    private Long commentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Notification() {
    }

    public Notification(String message, NotificationType type, User recipient) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
    }

    public Notification(String message, NotificationType type, User recipient, User sender) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
        this.sender = sender;
    }

    public Notification(String message, NotificationType type, User recipient, User sender, Long postId) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
        this.sender = sender;
        this.postId = postId;
    }

    public Notification(String message, NotificationType type, User recipient, User sender, Long postId, Long commentId) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
        this.sender = sender;
        this.postId = postId;
        this.commentId = commentId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


    // Helper methods
    public void markAsRead() {
        this.isRead = true;
    }
}

