package com.pesu.cfs.repository;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.ComplaintCategory;
import com.pesu.cfs.model.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findByCustomerOrderByCreatedAtDesc(User customer);
    List<Complaint> findByAssignedStaffOrderByCreatedAtDesc(User staff);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByCategory(ComplaintCategory category);
    List<Complaint> findByStatusAndAssignedStaff(ComplaintStatus status, User staff);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = ?1")
    long countByStatus(ComplaintStatus status);

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countByCategory();

    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    List<Complaint> findAllOrderByCreatedAtDesc();
}
