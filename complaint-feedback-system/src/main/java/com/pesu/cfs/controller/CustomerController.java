package com.pesu.cfs.controller;

import com.pesu.cfs.dto.ComplaintDto;
import com.pesu.cfs.dto.FeedbackDto;
import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.ComplaintCategory;
import com.pesu.cfs.model.enums.FeedbackType;
import com.pesu.cfs.model.enums.Priority;
import com.pesu.cfs.pattern.decorator.*;
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
@RequestMapping("/customer")
public class CustomerController {

    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;
    private final UserService userService;

    public CustomerController(ComplaintService complaintService,
                              FeedbackService feedbackService,
                              NotificationService notificationService,
                              UserService userService) {
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // ==================== COMPLAINT SUBMISSION ====================

    @GetMapping("/complaints/new")
    public String newComplaintForm(Model model) {
        model.addAttribute("complaint", new ComplaintDto());
        model.addAttribute("categories", ComplaintCategory.values());
        model.addAttribute("priorities", Priority.values());
        return "customer/complaint-form";
    }

    @PostMapping("/complaints/new")
    public String submitComplaint(@Valid @ModelAttribute("complaint") ComplaintDto dto,
                                  BindingResult result,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", ComplaintCategory.values());
            model.addAttribute("priorities", Priority.values());
            return "customer/complaint-form";
        }

        User customer = userService.findByUsername(auth.getName()).orElseThrow();
        Complaint complaint = complaintService.submitComplaint(dto, customer);

        redirectAttributes.addFlashAttribute("success",
                "Complaint submitted! Tracking ID: " + complaint.getComplaintId());
        return "redirect:/dashboard/home";
    }

    // ==================== COMPLAINT TRACKING ====================

    @GetMapping("/complaints/{id}")
    public String viewComplaint(@PathVariable Long id, Authentication auth, Model model) {
        Complaint complaint = complaintService.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // Decorator pattern: build decorated display text
        ComplaintDisplay display = new BasicComplaintDisplay(complaint);
        display = new PriorityDecorator(display, complaint.getPriority());
        display = new CategoryDecorator(display, complaint.getCategory());
        display = new TimestampDecorator(display, complaint.getCreatedAt());

        model.addAttribute("complaint", complaint);
        model.addAttribute("decoratedDescription", display.getDisplayText());
        model.addAttribute("feedbackDto", new FeedbackDto());
        model.addAttribute("feedbackTypes", FeedbackType.values());
        return "customer/complaint-detail";
    }

    /**
     * State: Resolved -> Closed (customerVerified)
     */
    @PostMapping("/complaints/{id}/close")
    public String closeComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.closeComplaint(id);
        redirectAttributes.addFlashAttribute("success", "Complaint closed. Thank you!");
        return "redirect:/dashboard/home";
    }

    /**
     * State: Resolved -> Reopened (notSatisfied)
     */
    @PostMapping("/complaints/{id}/reopen")
    public String reopenComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.reopenComplaint(id);
        redirectAttributes.addFlashAttribute("success", "Complaint reopened for reprocessing.");
        return "redirect:/dashboard/home";
    }

    // ==================== FEEDBACK ====================

    @PostMapping("/feedback")
    public String submitFeedback(@Valid @ModelAttribute("feedbackDto") FeedbackDto dto,
                                  BindingResult result,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid feedback. Please try again.");
            return "redirect:/dashboard/home";
        }

        User user = userService.findByUsername(auth.getName()).orElseThrow();
        feedbackService.submitFeedback(dto, user);
        redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully!");

        if (dto.getComplaintId() != null) {
            return "redirect:/customer/complaints/" + dto.getComplaintId();
        }
        return "redirect:/dashboard/home";
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.findByUser(user));
        model.addAttribute("user", user);
        return "customer/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.markAsRead(id);
        return "redirect:/customer/notifications";
    }

    @PostMapping("/notifications/{id}/archive")
    public String archiveNotification(@PathVariable Long id) {
        notificationService.archiveNotification(id);
        return "redirect:/customer/notifications";
    }
}
