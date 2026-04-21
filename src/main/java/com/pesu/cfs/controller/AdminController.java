package com.pesu.cfs.controller;

import com.pesu.cfs.dto.RegistrationDto;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.ComplaintStatus;
import com.pesu.cfs.model.enums.Role;
import com.pesu.cfs.service.ComplaintService;
import com.pesu.cfs.service.FeedbackService;
import com.pesu.cfs.service.NotificationService;
import com.pesu.cfs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;

    public AdminController(ComplaintService complaintService,
                           UserService userService,
                           FeedbackService feedbackService,
                           NotificationService notificationService) {
        this.complaintService = complaintService;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
    }

    // ==================== MANAGE COMPLAINTS ====================

    @GetMapping("/complaints")
    public String allComplaints(@RequestParam(required = false) ComplaintStatus status,
                                Model model, Authentication auth) {
        User admin = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", admin);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(admin));

        if (status != null) {
            model.addAttribute("complaints", complaintService.findByStatus(status));
            model.addAttribute("filterStatus", status);
        } else {
            model.addAttribute("complaints", complaintService.findAll());
        }
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("staffList", userService.findActiveStaff());
        return "admin/complaints";
    }

    /**
     * Manual assign complaint to specific staff member
     */
    @PostMapping("/complaints/{id}/assign")
    public String assignComplaint(@PathVariable Long id,
                                  @RequestParam Long staffId,
                                  RedirectAttributes redirectAttributes) {
        complaintService.assignComplaint(id, staffId);
        redirectAttributes.addFlashAttribute("success", "Complaint assigned successfully.");
        return "redirect:/admin/complaints";
    }

    /**
     * Auto-assign using Strategy pattern
     */
    @PostMapping("/complaints/{id}/auto-assign")
    public String autoAssign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            complaintService.autoAssignComplaint(id);
            redirectAttributes.addFlashAttribute("success", "Complaint auto-assigned (load-balanced).");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    /**
     * Escalate complaint (SLA exceeded)
     */
    @PostMapping("/complaints/{id}/escalate")
    public String escalate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.escalateComplaint(id);
        redirectAttributes.addFlashAttribute("success", "Complaint escalated.");
        return "redirect:/admin/complaints";
    }

    // ==================== MANAGE USERS ====================

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication auth) {
        User admin = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", admin);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(admin));
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("newStaff", new RegistrationDto());
        return "admin/users";
    }

    @PostMapping("/users/create-staff")
    public String createStaff(@Valid @ModelAttribute("newStaff") RegistrationDto dto,
                               BindingResult result,
                               @RequestParam Role role,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid staff details.");
            return "redirect:/admin/users";
        }
        try {
            userService.createStaff(dto, role);
            redirectAttributes.addFlashAttribute("success", "Staff account created.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam AccountStatus status,
                                RedirectAttributes redirectAttributes) {
        userService.updateAccountStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Account status updated.");
        return "redirect:/admin/users";
    }

    // ==================== REPORTS ====================

    @GetMapping("/reports")
    public String reports(Model model, Authentication auth) {
        User admin = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", admin);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(admin));
        model.addAttribute("statusCounts", complaintService.getStatusCounts());
        model.addAttribute("categoryCounts", complaintService.getCategoryCounts());
        model.addAttribute("avgRating", feedbackService.getOverallAverageRating());
        model.addAttribute("totalComplaints", complaintService.findAll().size());
        model.addAttribute("allFeedbacks", feedbackService.findAll());
        return "admin/reports";
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User admin = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.findByUser(admin));
        model.addAttribute("user", admin);
        return "customer/notifications"; // reuse same template
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/admin/notifications";
    }

    // ==================== FEEDBACK REVIEW ====================

    @GetMapping("/feedbacks")
    public String reviewFeedbacks(Model model, Authentication auth) {
        User admin = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", admin);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(admin));
        model.addAttribute("feedbacks", feedbackService.findAll());
        return "admin/feedbacks";
    }

    @PostMapping("/feedbacks/{id}/review")
    public String reviewFeedback(@PathVariable Long id,
                                  @RequestParam boolean approved,
                                  RedirectAttributes redirectAttributes) {
        feedbackService.reviewFeedback(id, approved);
        redirectAttributes.addFlashAttribute("success",
                "Feedback " + (approved ? "approved" : "rejected") + ".");
        return "redirect:/admin/feedbacks";
    }
}
