package com.sms.fee.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="fee_structures", uniqueConstraints=@UniqueConstraint(columnNames={"course_id","semester","category"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeStructure { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="course_id", nullable=false) private Long courseId; private Integer semester; @Column(nullable=false, length=80) private String category; @Column(nullable=false, precision=12, scale=2) private BigDecimal amount; @Column(length=20) @Builder.Default private String status="ACTIVE"; }
