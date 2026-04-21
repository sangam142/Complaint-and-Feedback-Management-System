package com.pesu.cfs.service;

import com.pesu.cfs.dto.ComplaintDto;
import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.ComplaintStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ComplaintService {
    Complaint submitComplaint(ComplaintDto dto, User customer);
    Optional<Complaint> findById(Long id);
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findByCustomer(User customer);
    List<Complaint> findByStaff(User staff);
    List<Complaint> findAll();
    List<Complaint> findByStatus(ComplaintStatus status);
    void assignComplaint(Long complaintId, Long staffId);
    void autoAssignComplaint(Long complaintId);
    void startWork(Long complaintId);
    void addResponse(Long complaintId, String response);
    void resolveComplaint(Long complaintId, String resolution);
    void escalateComplaint(Long complaintId);
    void closeComplaint(Long complaintId);
    void reopenComplaint(Long complaintId);
    Map<String, Long> getStatusCounts();
    List<Object[]> getCategoryCounts();
}
