package com.pesu.cfs.controller;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.pattern.decorator.*;
import com.pesu.cfs.service.ComplaintService;
import com.pesu.cfs.service.FeedbackService;
import com.pesu.cfs.service.NotificationService;
import com.pesu.cfs.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;
    private final UserService userService;

    public StaffController(ComplaintService complaintService,
                           FeedbackService feedbackService,
                           NotificationService notificationService,
                           UserService userService) {
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // ==================== VIEW ASSIGNED COMPLAINTS ====================

    @GetMapping("/complaints")
    public String myComplaints(Authentication auth, Model model) {
        User staff = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", staff);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(staff));
        model.addAttribute("complaints", complaintService.findByStaff(staff));
        return "staff/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaint(@PathVariable Long id, Model model, Authentication auth) {
        User staff = userService.findByUsername(auth.getName()).orElseThrow();
        Complaint complaint = complaintService.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // Decorator pattern
        ComplaintDisplay display = new BasicComplaintDisplay(complaint);
        display = new PriorityDecorator(display, complaint.getPriority());
        display = new CategoryDecorator(display, complaint.getCategory());
        display = new TimestampDecorator(display, complaint.getCreatedAt());

        model.addAttribute("user", staff);
        model.addAttribute("complaint", complaint);
        model.addAttribute("decoratedDescription", display.getDisplayText());
        model.addAttribute("feedbacks", feedbackService.findByUser(complaint.getCustomer()));
        return "staff/complaint-detail";
    }

    /**
     * State: Assigned -> InProgress (workStarted)
     */
    @PostMapping("/complaints/{id}/start")
    public String startWork(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.startWork(id);
        redirectAttributes.addFlashAttribute("success", "Work started on complaint.");
        return "redirect:/staff/complaints/" + id;
    }

    /**
     * State: InProgress self-loop — addResponse()
     */
    @PostMapping("/complaints/{id}/respond")
    public String addResponse(@PathVariable Long id,
                               @RequestParam String response,
                               RedirectAttributes redirectAttributes) {
        complaintService.addResponse(id, response);
        redirectAttributes.addFlashAttribute("success", "Response added.");
        return "redirect:/staff/complaints/" + id;
    }

    /**
     * State: InProgress -> Resolved (issueFixed)
     */
    @PostMapping("/complaints/{id}/resolve")
    public String resolve(@PathVariable Long id,
                          @RequestParam String resolution,
                          RedirectAttributes redirectAttributes) {
        complaintService.resolveComplaint(id, resolution);
        redirectAttributes.addFlashAttribute("success", "Complaint resolved.");
        return "redirect:/staff/complaints";
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User staff = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.findByUser(staff));
        model.addAttribute("user", staff);
        return "customer/notifications"; // reuse template
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/staff/notifications";
    }
}
