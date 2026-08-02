package com.sms.result.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="results", uniqueConstraints=@UniqueConstraint(columnNames={"student_id","exam_id","subject_id"}), indexes={@Index(name="ix_result_student", columnList="student_id"), @Index(name="ix_result_exam", columnList="exam_id")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="student_id", nullable=false) private Long studentId;
    @Column(name="exam_id", nullable=false) private Long examId;
    @Column(name="subject_id", nullable=false) private Long subjectId;
    @Column(name="marks_obtained", precision=8, scale=2) private BigDecimal marksObtained;
    @Column(name="max_marks", precision=8, scale=2) private BigDecimal maxMarks;
    @Column(length=5) private String grade;
    @Column(precision=4, scale=2) private BigDecimal gpa;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
    @PreUpdate void onUpdate(){ updatedAt=LocalDateTime.now(); }
}
