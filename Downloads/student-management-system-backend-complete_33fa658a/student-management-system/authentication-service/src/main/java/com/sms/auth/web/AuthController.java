package com.sms.auth.web;

import com.sms.auth.api.ApiResponse;
import com.sms.auth.dto.AuthDtos.*;
import com.sms.auth.entity.DeviceSession;
import com.sms.auth.service.AuthService;
import com.sms.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;
    private final JwtService jwt;

    @PostMapping("/otp/request")
    public ApiResponse<OtpRequestResponse> request(@Valid @RequestBody OtpRequest req, HttpServletRequest http) {
        return ApiResponse.ok(auth.requestOtp(req, http.getRemoteAddr()));
    }

    @PostMapping("/otp/resend")
    public ApiResponse<OtpRequestResponse> resend(@Valid @RequestBody OtpResend req, HttpServletRequest http) {
        return ApiResponse.ok(auth.resendOtp(req.otpId(), http.getRemoteAddr()));
    }

    @PostMapping("/otp/verify")
    public ApiResponse<TokenPair> verify(@Valid @RequestBody OtpVerify req, HttpServletRequest http) {
        return ApiResponse.ok(auth.verifyAndIssue(req, http.getRemoteAddr(), http.getHeader("User-Agent")));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenPair> refresh(@Valid @RequestBody RefreshReq req) {
        return ApiResponse.ok(auth.refresh(req.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest http) {
        Claims c = currentClaims(http);
        auth.logout(Long.valueOf(c.getSubject()), (String) c.get("deviceId"));
        return ApiResponse.ok("logged out", null);
    }

    @GetMapping("/me")
    public ApiResponse<UserView> me(HttpServletRequest http) {
        Claims c = currentClaims(http);
        return ApiResponse.ok(auth.me(Long.valueOf(c.getSubject())));
    }

    @GetMapping("/sessions")
    public ApiResponse<List<DeviceSession>> sessions(HttpServletRequest http) {
        Claims c = currentClaims(http);
        return ApiResponse.ok(auth.sessions(Long.valueOf(c.getSubject())));
    }

    @DeleteMapping("/sessions/{id}")
    public ApiResponse<Void> revoke(@PathVariable Long id, HttpServletRequest http) {
        Claims c = currentClaims(http);
        auth.revokeSession(Long.valueOf(c.getSubject()), id);
        return ApiResponse.ok(null);
    }

    private Claims currentClaims(HttpServletRequest http) {
        String h = http.getHeader(HttpHeaders.AUTHORIZATION);
        if (h == null || !h.startsWith("Bearer ")) throw new IllegalArgumentException("Missing token");
        return jwt.verify(h.substring(7)).getPayload();
    }
}
