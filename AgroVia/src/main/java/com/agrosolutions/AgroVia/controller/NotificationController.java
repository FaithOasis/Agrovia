package com.agrosolutions.AgroVia.controller;

import com.agrosolutions.AgroVia.entity.Notification;
import com.agrosolutions.AgroVia.service.NotificationService;
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

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get all notifications for current user
    @GetMapping
    public ResponseEntity<?> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            String username = getCurrentUsername();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Notification> notifications = notificationService.getUserNotifications(username, pageable);

            return ResponseEntity.ok(createPageResponse(notifications, "Notifications retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get unread notifications for current user
    @GetMapping("/unread")
    public ResponseEntity<?> getMyUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            String username = getCurrentUsername();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Notification> notifications = notificationService.getUnreadNotifications(username, pageable);

            return ResponseEntity.ok(createPageResponse(notifications, "Unread notifications retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get unread notification count
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadNotificationCount() {
        try {
            String username = getCurrentUsername();
            long count = notificationService.getUnreadNotificationCount(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("message", "Unread notification count retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Mark all notifications as read
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead() {
        try {
            String username = getCurrentUsername();
            notificationService.markAllAsRead(username);

            return ResponseEntity.ok(createSuccessResponse("All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Mark specific notification as read
    @PostMapping("/{id}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            notificationService.markAsRead(id, username);

            return ResponseEntity.ok(createSuccessResponse("Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            String username = getCurrentUsername();
            notificationService.deleteNotification(id, username);

            return ResponseEntity.ok(createSuccessResponse("Notification deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Clear all read notifications
    @DeleteMapping("/clear-read")
    public ResponseEntity<?> clearReadNotifications() {
        try {
            // Note: This would require adding a method to NotificationService
            // For now, we'll return a message about this feature
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Feature coming soon: This will clear all read notifications");

            return ResponseEntity.ok(response);
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
    private Map<String, Object> createPageResponse(Page<Notification> notifications, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", notifications.getContent());
        response.put("currentPage", notifications.getNumber());
        response.put("totalItems", notifications.getTotalElements());
        response.put("totalPages", notifications.getTotalPages());
        response.put("unreadCount", notificationService.getUnreadNotificationCount(getCurrentUsername()));
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
}