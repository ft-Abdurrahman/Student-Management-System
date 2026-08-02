package com.sms.notice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Entity @Table(name="notices", indexes={@Index(name="ix_notice_audience", columnList="audience"), @Index(name="ix_notice_pinned", columnList="pinned")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notice {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=180) private String title;
    @Column(length=5000) private String body;
    @Column(nullable=false, length=30) private String audience;
    @Column(name="department_id") private Long departmentId;
    @Column(nullable=false) @Builder.Default private boolean pinned=false;
    @Column(length=120) private String author;
    @Column(name="publish_at") private LocalDateTime publishAt;
    @Column(name="expires_at") private LocalDateTime expiresAt;
    @Column(nullable=false, length=20) @Builder.Default private String status="PUBLISHED";
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); if(publishAt==null) publishAt=createdAt; }
}
