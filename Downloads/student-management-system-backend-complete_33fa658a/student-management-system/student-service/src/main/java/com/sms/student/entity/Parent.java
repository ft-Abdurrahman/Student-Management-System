package com.sms.student.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "parents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "father_name", length = 120)  private String fatherName;
    @Column(name = "father_phone", length = 20)  private String fatherPhone;
    @Column(name = "mother_name", length = 120)  private String motherName;
    @Column(name = "mother_phone", length = 20)  private String motherPhone;
    @Column(name = "guardian_name", length = 120) private String guardianName;
    @Column(name = "guardian_phone", length = 20) private String guardianPhone;
}
