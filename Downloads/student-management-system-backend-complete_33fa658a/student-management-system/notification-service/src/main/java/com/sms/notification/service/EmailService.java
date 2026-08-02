package com.sms.notification.service;

import com.sms.notification.entity.EmailLog;
import com.sms.notification.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository logRepo;

    @Value("${sms.notification.sender}") private String from;
    @Value("${sms.notification.sender-name:SMS University}") private String fromName;

    @Async
    public void send(String to, String subject, String html, String template) {
        EmailLog entry = EmailLog.builder()
                .toAddress(to).subject(subject).template(template).status("QUEUED").build();
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            entry.setStatus("SENT");
            entry.setSentAt(LocalDateTime.now());
            log.info("[mail] sent to={} subject='{}'", to, subject);
        } catch (Exception ex) {
            entry.setStatus("FAILED");
            entry.setError(ex.getMessage());
            log.error("[mail] failed to {}: {}", to, ex.getMessage());
        }
        try { logRepo.save(entry); } catch (Exception ignore) {}
    }

    public String renderOtp(String code, int minutes) {
        return """
            <div style="font-family:Poppins,Arial,sans-serif;background:#F8FAFC;padding:24px">
              <div style="max-width:520px;margin:0 auto;background:#fff;border-radius:12px;padding:32px;border:1px solid #E2E8F0">
                <div style="display:flex;align-items:center;gap:10px;margin-bottom:18px">
                  <div style="width:40px;height:40px;border-radius:10px;background:linear-gradient(135deg,#2563EB,#14B8A6);color:#fff;display:grid;place-items:center;font-weight:700">S</div>
                  <strong style="font-size:16px;color:#0F172A">SMS University</strong>
                </div>
                <h2 style="color:#0F172A;margin:0 0 8px">Your verification code</h2>
                <p style="color:#64748B;margin:0 0 24px">Use the code below to sign in. It expires in %d minutes.</p>
                <div style="font-size:34px;font-weight:700;letter-spacing:10px;text-align:center;background:#F1F5F9;border-radius:12px;padding:18px;color:#2563EB">%s</div>
                <p style="color:#94A3B8;font-size:12px;margin-top:24px">If you did not request this code, you can safely ignore this email.</p>
              </div>
            </div>""".formatted(minutes, code);
    }

    public String renderWelcome(String name) {
        return """
            <div style="font-family:Poppins,Arial,sans-serif;background:#F8FAFC;padding:24px">
              <div style="max-width:520px;margin:0 auto;background:#fff;border-radius:12px;padding:32px;border:1px solid #E2E8F0">
                <h2 style="color:#0F172A">Welcome to SMS University, %s!</h2>
                <p style="color:#64748B">Your account is ready. Sign in any time using your registered email or mobile.</p>
                <a href="http://localhost:3000/login.html" style="display:inline-block;margin-top:16px;background:#2563EB;color:#fff;padding:10px 18px;border-radius:8px;text-decoration:none">Open dashboard</a>
              </div>
            </div>""".formatted(name);
    }
}
