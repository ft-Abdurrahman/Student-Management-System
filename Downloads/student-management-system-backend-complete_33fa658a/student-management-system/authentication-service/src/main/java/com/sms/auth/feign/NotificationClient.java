package com.sms.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/otp/send")
    Map<String, Object> sendOtp(@RequestBody Map<String, String> body);

    @PostMapping("/api/v1/notifications/otp/verify")
    Map<String, Object> verifyOtp(@RequestBody Map<String, String> body);

    @PostMapping("/api/v1/notifications/otp/resend")
    Map<String, Object> resendOtp(@RequestParam("otpId") String otpId);

    @PostMapping("/api/v1/notifications/welcome")
    Map<String, Object> welcome(@RequestParam("to") String to, @RequestParam("name") String name);
}
