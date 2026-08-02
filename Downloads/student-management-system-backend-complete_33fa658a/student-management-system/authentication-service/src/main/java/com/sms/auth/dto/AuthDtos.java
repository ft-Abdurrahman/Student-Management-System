package com.sms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public class AuthDtos {
    public record OtpRequest(@NotBlank String destination,
                             @Pattern(regexp = "EMAIL|SMS") String channel) {}
    public record OtpRequestResponse(String otpId, int expiresIn) {}

    public record OtpVerify(@NotBlank String otpId, @NotBlank String code,
                            @NotBlank String deviceId, String deviceName, boolean remember) {}

    public record TokenPair(String accessToken, String refreshToken, long expiresIn, UserView user) {}

    public record RefreshReq(@NotBlank String refreshToken) {}

    public record UserView(Long id, String email, String phone, String fullName, Set<String> roles) {}

    public record OtpResend(@NotBlank String otpId) {}
}
