package com.pesu.cfs.pattern.observer;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.Notification;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.Role;
import com.pesu.cfs.pattern.factory.NotificationFactory;
import com.pesu.cfs.repository.NotificationRepository;
import com.pesu.cfs.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer: Creates and persists notifications
 * using the NotificationFactory when complaint events occur.
 */
@Component
public class NotificationObserver implements ComplaintObserver {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationObserver(NotificationRepository notificationRepository,
                                UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onComplaintSubmitted(Complaint complaint) {
        Notification n = NotificationFactory.createComplaintSubmitted(complaint, complaint.getCustomer());
        n.send();
        n.deliver();
        notificationRepository.save(n);
    }

    @Override
    public void onComplaintAssigned(Complaint complaint) {
        if (complaint.getAssignedStaff() != null) {
            Notification n = NotificationFactory.createComplaintAssigned(complaint, complaint.getAssignedStaff());
            n.send();
            n.deliver();
            notificationRepository.save(n);
        }
        // Also notify customer
        Notification nc = NotificationFactory.createStatusUpdate(complaint, complaint.getCustomer());
        nc.send();
        nc.deliver();
        notificationRepository.save(nc);
    }

    @Override
    public void onComplaintStatusChanged(Complaint complaint) {
        Notification n = NotificationFactory.createStatusUpdate(complaint, complaint.getCustomer());
        n.send();
        n.deliver();
        notificationRepository.save(n);
    }

    @Override
    public void onComplaintResolved(Complaint complaint) {
        Notification n = NotificationFactory.createComplaintResolved(complaint, complaint.getCustomer());
        n.send();
        n.deliver();
        notificationRepository.save(n);
    }

    @Override
    public void onComplaintEscalated(Complaint complaint) {
        // Notify all admins
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            Notification n = NotificationFactory.createComplaintEscalated(complaint, admin);
            n.send();
            n.deliver();
            notificationRepository.save(n);
        }
    }

    @Override
    public void onComplaintReopened(Complaint complaint) {
        if (complaint.getAssignedStaff() != null) {
            Notification n = NotificationFactory.createComplaintReopened(complaint, complaint.getAssignedStaff());
            n.send();
            n.deliver();
            notificationRepository.save(n);
        }
    }
}
