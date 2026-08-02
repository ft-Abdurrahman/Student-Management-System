package com.sms.timetable.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="timetable_slots", indexes={@Index(name="ix_slot_teacher", columnList="teacher_id"), @Index(name="ix_slot_course_sem", columnList="course_id,semester")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimetableSlot {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="course_id") private Long courseId;
    private Integer semester;
    @Column(name="subject_id", nullable=false) private Long subjectId;
    @Column(name="teacher_id", nullable=false) private Long teacherId;
    @Column(name="day_of_week", nullable=false, length=12) private String dayOfWeek;
    @Column(name="start_time", nullable=false) private LocalTime startTime;
    @Column(name="end_time", nullable=false) private LocalTime endTime;
    @Column(length=40) private String room;
    @Column(nullable=false, length=20) @Builder.Default private String status="ACTIVE";
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
}
