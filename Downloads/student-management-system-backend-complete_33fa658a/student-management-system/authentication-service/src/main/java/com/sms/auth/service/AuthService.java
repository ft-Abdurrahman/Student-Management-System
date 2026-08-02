package com.sms.auth.service;

import com.sms.auth.dto.AuthDtos.*;
import com.sms.auth.entity.*;
import com.sms.auth.feign.NotificationClient;
import com.sms.auth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final RefreshTokenRepository refreshRepo;
    private final DeviceSessionRepository sessionRepo;
    private final AuditLogRepository auditRepo;
    private final NotificationClient notifications;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    @Value("${sms.jwt.refresh-ttl-days:30}") private long refreshDays;
    @Value("${sms.jwt.refresh-ttl-days-remembered:90}") private long rememberedDays;

    @Transactional
    public OtpRequestResponse requestOtp(OtpRequest req, String ip) {
        // Find or auto-create the user — admins should pre-provision in production
        if ("EMAIL".equals(req.channel())) {
            users.findByEmail(req.destination()).orElseGet(() ->
                    users.save(User.builder().email(req.destination()).fullName(deriveName(req.destination()))
                            .roles(new HashSet<>(Set.of("STUDENT"))).build()));
        } else {
            users.findByPhone(req.destination()).orElseGet(() ->
                    users.save(User.builder().email(req.destination() + "@phone.local").phone(req.destination())
                            .fullName("User " + req.destination().substring(Math.max(0, req.destination().length() - 4)))
                            .roles(new HashSet<>(Set.of("STUDENT"))).build()));
        }
        var resp = notifications.sendOtp(Map.of("destination", req.destination(), "channel", req.channel()));
        var data = extractData(resp);
        audit(null, "OTP_REQUEST", ip, "dest=" + req.destination() + " channel=" + req.channel());
        return new OtpRequestResponse((String) data.get("otpId"), ((Number) data.get("expiresIn")).intValue());
    }

    @Transactional
    public OtpRequestResponse resendOtp(String otpId, String ip) {
        var resp = notifications.resendOtp(otpId);
        var data = extractData(resp);
        audit(null, "OTP_RESEND", ip, "otpId=" + otpId);
        return new OtpRequestResponse((String) data.get("otpId"), ((Number) data.get("expiresIn")).intValue());
    }

    @Transactional
    public TokenPair verifyAndIssue(OtpVerify req, String ip, String userAgent) {
        var verify = notifications.verifyOtp(Map.of("otpId", req.otpId(), "code", req.code()));
        var data = extractData(verify);
        if (!Boolean.TRUE.equals(data.get("valid"))) {
            audit(null, "OTP_VERIFY_FAIL", ip, String.valueOf(data.get("reason")));
            throw new IllegalArgumentException(String.valueOf(data.getOrDefault("reason", "Invalid OTP")));
        }
        String dest = (String) data.get("destination");
        String channel = (String) data.get("channel");
        User u = "EMAIL".equals(channel)
                ? users.findByEmail(dest).orElseThrow()
                : users.findByPhone(dest).orElseThrow();

        if ("EMAIL".equals(channel)) u.setEmailVerified(true); else u.setPhoneVerified(true);
        users.save(u);

        // Upsert device session
        var session = sessionRepo.findByUserIdAndDeviceId(u.getId(), req.deviceId())
                .orElse(DeviceSession.builder().userId(u.getId()).deviceId(req.deviceId()).build());
        session.setDeviceName(req.deviceName());
        session.setIpAddress(ip);
        session.setUserAgent(userAgent);
        session.setRemembered(req.remember());
        session.setLastSeenAt(LocalDateTime.now());
        sessionRepo.save(session);

        TokenPair tp = mintTokens(u, req.deviceId(), req.remember());
        audit(u.getId(), "LOGIN", ip, "device=" + req.deviceId());
        return tp;
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        String hash = encoder.encode(refreshToken); // for log only; actual lookup below
        var stored = refreshRepo.findAll().stream()
                .filter(rt -> rt.getRevokedAt() == null
                        && rt.getExpiresAt().isAfter(LocalDateTime.now())
                        && encoder.matches(refreshToken, rt.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        User u = users.findById(stored.getUserId()).orElseThrow();
        // rotate
        stored.setRevokedAt(LocalDateTime.now());
        refreshRepo.save(stored);
        return mintTokens(u, stored.getDeviceId(), false);
    }

    @Transactional
    public void logout(Long userId, String deviceId) {
        refreshRepo.findByUserId(userId).forEach(rt -> {
            if (rt.getRevokedAt() == null && Objects.equals(rt.getDeviceId(), deviceId)) {
                rt.setRevokedAt(LocalDateTime.now()); refreshRepo.save(rt);
            }
        });
        sessionRepo.findByUserIdAndDeviceId(userId, deviceId).ifPresent(sessionRepo::delete);
        audit(userId, "LOGOUT", null, "device=" + deviceId);
    }

    public UserView me(Long userId) {
        var u = users.findById(userId).orElseThrow();
        return new UserView(u.getId(), u.getEmail(), u.getPhone(), u.getFullName(), u.getRoles());
    }

    public List<DeviceSession> sessions(Long userId) { return sessionRepo.findByUserId(userId); }

    @Transactional public void revokeSession(Long userId, Long sessionId) {
        sessionRepo.findById(sessionId)
                .filter(s -> s.getUserId().equals(userId))
                .ifPresent(sessionRepo::delete);
    }

    /* ---------- internal helpers ---------- */
    private TokenPair mintTokens(User u, String deviceId, boolean remembered) {
        String access = jwt.issueAccess(u, deviceId);
        String refreshPlain = randomRefresh();
        long days = remembered ? rememberedDays : refreshDays;
        refreshRepo.save(RefreshToken.builder()
                .userId(u.getId()).deviceId(deviceId)
                .tokenHash(encoder.encode(refreshPlain))
                .expiresAt(LocalDateTime.now().plusDays(days)).build());
        return new TokenPair(access, refreshPlain, jwt.getAccessTtlSec(),
                new UserView(u.getId(), u.getEmail(), u.getPhone(), u.getFullName(), u.getRoles()));
    }

    private String randomRefresh() {
        byte[] bytes = new byte[48]; random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void audit(Long userId, String action, String ip, String details) {
        try { auditRepo.save(AuditLog.builder().userId(userId).action(action).ipAddress(ip).details(details).build()); }
        catch (Exception ignore) {}
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Map<String, Object> envelope) {
        Object d = envelope.get("data");
        return d instanceof Map ? (Map<String, Object>) d : envelope;
    }

    private String deriveName(String email) {
        int at = email.indexOf('@'); String local = at > 0 ? email.substring(0, at) : email;
        local = local.replaceAll("[._-]+", " ");
        return Arrays.stream(local.split(" "))
                .filter(s -> !s.isBlank())
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .reduce((a, b) -> a + " " + b).orElse(local);
    }
}
