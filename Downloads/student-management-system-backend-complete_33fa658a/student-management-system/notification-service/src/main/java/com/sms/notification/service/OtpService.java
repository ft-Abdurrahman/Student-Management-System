package com.sms.notification.service;

import com.sms.notification.dto.NotificationDtos.*;
import com.sms.notification.entity.OtpCode;
import com.sms.notification.repository.OtpCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository repo;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    @Value("${sms.notification.otp.length:6}") private int length;
    @Value("${sms.notification.otp.ttl-seconds:300}") private int ttlSeconds;
    @Value("${sms.notification.otp.max-attempts:5}") private int maxAttempts;
    @Value("${sms.notification.otp.resend-cooldown-seconds:30}") private int cooldownSeconds;

    @Transactional
    public SendOtpResponse send(SendOtpRequest req) {
        String code = generateCode();
        String otpId = UUID.randomUUID().toString();

        OtpCode entity = OtpCode.builder()
                .otpId(otpId)
                .destination(req.destination())
                .channel(req.channel())
                .codeHash(encoder.encode(code))
                .attempts(0)
                .expiresAt(LocalDateTime.now().plusSeconds(ttlSeconds))
                .createdAt(LocalDateTime.now())
                .lastSentAt(LocalDateTime.now())
                .build();
        repo.save(entity);

        if ("EMAIL".equals(req.channel())) {
            emailService.send(req.destination(), "Your SMS University OTP",
                    emailService.renderOtp(code, ttlSeconds / 60), "otp");
        } else {
            // SMS provider integration is plug-able; log for now.
            log.info("[OTP][SMS->{}]: OTP generated", req.destination());
        }
        log.info("[OTP] issued otpId={} dest={} channel={} (ttl={}s)",
                otpId, req.destination(), req.channel(), ttlSeconds);
        return new SendOtpResponse(otpId, ttlSeconds);
    }

    @Transactional
    public VerifyOtpResponse verify(VerifyOtpRequest req) {
        var opt = repo.findByOtpId(req.otpId());
        if (opt.isEmpty()) return new VerifyOtpResponse(false, null, null, "OTP not found");
        var otp = opt.get();
        if (otp.getConsumedAt() != null) return new VerifyOtpResponse(false, null, null, "Already used");
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) return new VerifyOtpResponse(false, null, null, "Expired");
        if (otp.getAttempts() >= maxAttempts) return new VerifyOtpResponse(false, null, null, "Too many attempts");

        otp.setAttempts(otp.getAttempts() + 1);
        if (!encoder.matches(req.code(), otp.getCodeHash())) {
            repo.save(otp);
            return new VerifyOtpResponse(false, null, null, "Invalid code");
        }
        otp.setConsumedAt(LocalDateTime.now());
        repo.save(otp);
        return new VerifyOtpResponse(true, otp.getDestination(), otp.getChannel(), "OK");
    }

    @Transactional
    public SendOtpResponse resend(String otpId) {
        var otp = repo.findByOtpId(otpId).orElseThrow(() -> new IllegalArgumentException("Unknown otpId"));
        if (otp.getLastSentAt() != null && otp.getLastSentAt().plusSeconds(cooldownSeconds).isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Please wait before resending");
        }
        return send(new SendOtpRequest(otp.getDestination(), otp.getChannel()));
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, length);
        return String.format("%0" + length + "d", random.nextInt(bound));
    }
}
