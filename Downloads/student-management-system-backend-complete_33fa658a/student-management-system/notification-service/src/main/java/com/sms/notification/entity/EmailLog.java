package com.sms.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Table(name = "email_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_address", nullable = false, length = 200)
    private String toAddress;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(length = 50)
    private String template;

    @Column(nullable = false, length = 20)
    private String status; // SENT | FAILED | QUEUED

    @Column(columnDefinition = "TEXT")
    private String error;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
