package com.sms.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Table(name = "otp_codes", indexes = {
        @Index(name = "ix_otp_destination", columnList = "destination"),
        @Index(name = "ix_otp_expires_at", columnList = "expires_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String otpId;          // opaque token shared with auth-service

    @Column(nullable = false, length = 200)
    private String destination;    // email or phone

    @Column(nullable = false, length = 10)
    private String channel;        // EMAIL | SMS

    @Column(name = "code_hash", nullable = false, length = 80)
    private String codeHash;       // BCrypt hash of the 6-digit code

    @Column(nullable = false)
    private int attempts;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
