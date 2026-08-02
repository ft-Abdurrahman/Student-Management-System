package com.sms.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "students",
    uniqueConstraints = { @UniqueConstraint(columnNames = "roll_number"),
                          @UniqueConstraint(columnNames = "admission_number"),
                          @UniqueConstraint(columnNames = "email") },
    indexes = { @Index(name = "ix_stu_dept", columnList = "department_id"),
                @Index(name = "ix_stu_course", columnList = "course_id"),
                @Index(name = "ix_stu_status", columnList = "status") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, length = 30)
    private String rollNumber;

    @Column(name = "admission_number", nullable = false, length = 30)
    private String admissionNumber;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(length = 1)
    private String gender;    // M/F/O

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "photo_url", length = 300)
    private String photoUrl;

    @Column(name = "department_id")
    private Long departmentId;   // logical FK

    @Column(name = "course_id")
    private Long courseId;       // logical FK

    private Integer semester;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";      // ACTIVE|INACTIVE|GRADUATED|SUSPENDED

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Parent parent;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Address address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
