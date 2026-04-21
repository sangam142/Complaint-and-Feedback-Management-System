package com.pesu.cfs.repository;

import com.pesu.cfs.model.Notification;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndStatusIn(User user, List<NotificationStatus> statuses);
    long countByUserAndStatusIn(User user, List<NotificationStatus> statuses);
}
