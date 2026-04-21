package com.pesu.cfs.service;

import com.pesu.cfs.model.Notification;
import com.pesu.cfs.model.User;

import java.util.List;

public interface NotificationService {
    List<Notification> findByUser(User user);
    long getUnreadCount(User user);
    void markAsRead(Long notificationId);
    void archiveNotification(Long notificationId);
}
