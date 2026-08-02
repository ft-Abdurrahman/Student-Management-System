package com.sms.attendance.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="leave_requests", indexes=@Index(name="ix_leave_student", columnList="student_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="student_id", nullable=false) private Long studentId;
    @Column(name="from_date", nullable=false) private LocalDate fromDate;
    @Column(name="to_date", nullable=false) private LocalDate toDate;
    @Column(length=2000) private String reason;
    @Column(nullable=false, length=20) @Builder.Default private String status="PENDING";
    @Column(name="reviewed_by") private Long reviewedBy;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @Column(name="reviewed_at") private LocalDateTime reviewedAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
}
