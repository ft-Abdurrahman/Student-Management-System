package com.sms.attendance.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="attendance", uniqueConstraints=@UniqueConstraint(columnNames={"student_id","subject_id","attendance_date"}), indexes={@Index(name="ix_att_student", columnList="student_id"), @Index(name="ix_att_subject_date", columnList="subject_id,attendance_date")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="student_id", nullable=false) private Long studentId;
    @Column(name="subject_id", nullable=false) private Long subjectId;
    @Column(name="attendance_date", nullable=false) private LocalDate attendanceDate;
    @Column(nullable=false, length=20) private String status;
    @Column(name="marked_by_teacher_id") private Long markedByTeacherId;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
}
