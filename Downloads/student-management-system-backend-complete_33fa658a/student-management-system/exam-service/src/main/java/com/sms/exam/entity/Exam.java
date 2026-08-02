package com.sms.exam.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="exams", indexes={@Index(name="ix_exam_course", columnList="course_id"), @Index(name="ix_exam_semester", columnList="semester")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=160) private String name;
    @Column(nullable=false, length=30) private String type;
    @Column(name="course_id") private Long courseId;
    private Integer semester;
    @Column(name="start_date") private LocalDate startDate;
    @Column(name="end_date") private LocalDate endDate;
    @Column(nullable=false, length=20) @Builder.Default private String status="SCHEDULED";
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
}
