package com.sms.attendance.repository;
import com.sms.attendance.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> { List<LeaveRequest> findByStudentId(Long studentId); }
