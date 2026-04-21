package com.pesu.cfs.controller;

import com.pesu.cfs.model.User;
import com.pesu.cfs.service.ComplaintService;
import com.pesu.cfs.service.FeedbackService;
import com.pesu.cfs.service.NotificationService;
import com.pesu.cfs.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;

    public DashboardController(UserService userService,
                               ComplaintService complaintService,
                               FeedbackService feedbackService,
                               NotificationService notificationService) {
        this.userService = userService;
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public String home(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return switch (user.getRole()) {
            case ADMIN -> {
                model.addAttribute("statusCounts", complaintService.getStatusCounts());
                model.addAttribute("allComplaints", complaintService.findAll());
                model.addAttribute("avgRating", feedbackService.getOverallAverageRating());
                model.addAttribute("allUsers", userService.findAll());
                yield "admin/dashboard";
            }
            case SUPPORT_STAFF -> {
                model.addAttribute("assignedComplaints", complaintService.findByStaff(user));
                yield "staff/dashboard";
            }
            case CUSTOMER -> {
                model.addAttribute("myComplaints", complaintService.findByCustomer(user));
                model.addAttribute("myFeedbacks", feedbackService.findByUser(user));
                yield "customer/dashboard";
            }
        };
    }
}
