package com.sms.student.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(length = 200) private String line1;
    @Column(length = 200) private String line2;
    @Column(length = 80)  private String city;
    @Column(length = 80)  private String state;
    @Column(length = 80)  private String country;
    @Column(name = "postal_code", length = 20) private String postalCode;
}
