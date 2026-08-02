package com.sms.course.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = "code"), indexes = @Index(name = "ix_course_department", columnList = "department_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 40) private String code;
    @Column(nullable = false, length = 160) private String name;
    private Integer credits;
    @Column(name = "duration_semesters") private Integer durationSemesters;
    @Column(name = "department_id") private Long departmentId;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt = LocalDateTime.now(); }
}
