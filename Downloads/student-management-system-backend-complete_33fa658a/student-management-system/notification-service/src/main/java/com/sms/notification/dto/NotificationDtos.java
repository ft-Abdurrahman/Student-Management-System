package com.sms.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class NotificationDtos {
    public record SendEmailRequest(@NotBlank String to, @NotBlank String subject,
                                   @NotBlank String body, String template) {}

    public record SendOtpRequest(@NotBlank String destination,
                                 @Pattern(regexp = "EMAIL|SMS") String channel) {}
    public record SendOtpResponse(String otpId, int expiresIn) {}

    public record VerifyOtpRequest(@NotBlank String otpId, @NotBlank String code) {}
    public record VerifyOtpResponse(boolean valid, String destination, String channel, String reason) {}
}
