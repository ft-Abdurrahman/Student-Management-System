package com.sms.notification.web;

import com.sms.notification.api.ApiResponse;
import com.sms.notification.dto.NotificationDtos.*;
import com.sms.notification.service.EmailService;
import com.sms.notification.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final OtpService otpService;
    private final EmailService emailService;

    @PostMapping("/email")
    public ApiResponse<Void> email(@Valid @RequestBody SendEmailRequest req) {
        emailService.send(req.to(), req.subject(), req.body(), req.template());
        return ApiResponse.ok("queued", null);
    }

    @PostMapping("/otp/send")
    public ApiResponse<SendOtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest req) {
        return ApiResponse.ok(otpService.send(req));
    }

    @PostMapping("/otp/verify")
    public ApiResponse<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        return ApiResponse.ok(otpService.verify(req));
    }

    @PostMapping("/otp/resend")
    public ApiResponse<SendOtpResponse> resend(@RequestParam String otpId) {
        return ApiResponse.ok(otpService.resend(otpId));
    }

    @PostMapping("/welcome")
    public ApiResponse<Void> welcome(@RequestParam String to, @RequestParam String name) {
        emailService.send(to, "Welcome to SMS University", emailService.renderWelcome(name), "welcome");
        return ApiResponse.ok(null);
    }
}
