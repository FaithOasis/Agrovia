package repository;

import entity.Notification;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a user with pagination
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    // Find unread notifications for a user
    Page<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient, Pageable pageable);

    // Count unread notifications for a user
    long countByRecipientAndIsReadFalse(User recipient);

    // Find notifications by type for a user
    Page<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(User recipient, String type, Pageable pageable);

    // Mark all notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient = :recipient AND n.isRead = false")
    int markAllAsRead(@Param("recipient") User recipient);

    // Mark specific notification as read
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.recipient = :recipient")
    int markAsRead(@Param("id") Long id, @Param("recipient") User recipient);

    // Delete old read notifications (cleanup)
    @Modifying
    @Transactional
    void deleteByRecipientAndIsReadTrue(User recipient);

    // Find notifications by post (for cleanup when post is deleted)
    List<Notification> findByPostId(Long postId);

    // Find notifications by comment (for cleanup when comment is deleted)
    List<Notification> findByCommentId(Long commentId);
}