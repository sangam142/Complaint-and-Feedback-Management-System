package com.pesu.cfs.service.impl;

import com.pesu.cfs.model.Notification;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.NotificationStatus;
import com.pesu.cfs.repository.NotificationRepository;
import com.pesu.cfs.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndStatusIn(user,
                List.of(NotificationStatus.DELIVERED, NotificationStatus.UNREAD));
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.markRead();
        notificationRepository.save(n);
    }

    @Override
    public void archiveNotification(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.archive();
        notificationRepository.save(n);
    }
}
