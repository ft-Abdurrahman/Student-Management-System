package com.sms.fee.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="student_fees", indexes=@Index(name="ix_fee_student", columnList="student_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentFee { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="student_id", nullable=false) private Long studentId; @Column(name="course_id") private Long courseId; private Integer semester; @Column(nullable=false, precision=12, scale=2) private BigDecimal total; @Column(nullable=false, precision=12, scale=2) @Builder.Default private BigDecimal paid=BigDecimal.ZERO; @Column(precision=12, scale=2) private BigDecimal due; @Column(length=20) @Builder.Default private String status="PENDING"; @Column(name="due_date") private LocalDate dueDate; @PrePersist @PreUpdate void calc(){ if(paid==null) paid=BigDecimal.ZERO; if(total==null) total=BigDecimal.ZERO; due=total.subtract(paid); status=due.compareTo(BigDecimal.ZERO)<=0?"PAID":paid.compareTo(BigDecimal.ZERO)>0?"PARTIAL":"PENDING"; } }
