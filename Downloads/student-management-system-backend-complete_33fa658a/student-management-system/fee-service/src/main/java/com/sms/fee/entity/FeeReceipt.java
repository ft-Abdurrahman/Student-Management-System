package com.sms.fee.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="fee_receipts", indexes=@Index(name="ix_receipt_student", columnList="student_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeReceipt { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="student_id", nullable=false) private Long studentId; @Column(name="student_fee_id") private Long studentFeeId; @Column(nullable=false, precision=12, scale=2) private BigDecimal amount; @Column(length=40) private String method; @Column(length=120) private String reference; @Column(name="paid_at", nullable=false) private LocalDateTime paidAt; @PrePersist void onCreate(){ if(paidAt==null) paidAt=LocalDateTime.now(); } }
