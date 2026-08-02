package com.sms.subject.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "subjects", uniqueConstraints = @UniqueConstraint(columnNames = "code"), indexes = {@Index(name="ix_subject_course", columnList="course_id"), @Index(name="ix_subject_teacher", columnList="teacher_id")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String code;
    @Column(nullable = false, length = 180) private String name;
    private Integer credits;
    private Integer semester;
    @Column(name = "course_id") private Long courseId;
    @Column(name = "teacher_id") private Long teacherId;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt = LocalDateTime.now(); }
}
