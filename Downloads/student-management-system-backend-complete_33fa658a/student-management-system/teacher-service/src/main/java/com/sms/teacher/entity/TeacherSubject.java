package com.sms.teacher.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_subjects", uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "subject_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeacherSubject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "teacher_id", nullable = false) private Long teacherId;
    @Column(name = "subject_id", nullable = false) private Long subjectId;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
