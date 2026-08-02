package com.sms.exam.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="exam_schedules", indexes=@Index(name="ix_schedule_exam", columnList="exam_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExamSchedule {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="exam_id", nullable=false) private Long examId;
    @Column(name="subject_id", nullable=false) private Long subjectId;
    @Column(name="exam_date", nullable=false) private LocalDate examDate;
    @Column(name="start_time") private LocalTime startTime;
    @Column(name="end_time") private LocalTime endTime;
    @Column(length=80) private String hall;
    @Column(name="invigilator_teacher_id") private Long invigilatorTeacherId;
}
