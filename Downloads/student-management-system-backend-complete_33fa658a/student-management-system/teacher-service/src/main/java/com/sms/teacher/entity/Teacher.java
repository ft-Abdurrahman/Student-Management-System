package com.sms.teacher.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers", uniqueConstraints = {
    @UniqueConstraint(columnNames = "employee_code"), @UniqueConstraint(columnNames = "email")
}, indexes = {@Index(name = "ix_teacher_department", columnList = "department_id"), @Index(name = "ix_teacher_status", columnList = "status")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "employee_code", nullable = false, length = 40) private String employeeCode;
    @Column(name = "first_name", nullable = false, length = 80) private String firstName;
    @Column(name = "last_name", length = 80) private String lastName;
    @Column(nullable = false, length = 160) private String email;
    @Column(length = 30) private String phone;
    @Column(name = "department_id") private Long departmentId;
    @Column(length = 120) private String qualification;
    @Column(name = "experience_years") private Integer experienceYears;
    private BigDecimal salary;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
