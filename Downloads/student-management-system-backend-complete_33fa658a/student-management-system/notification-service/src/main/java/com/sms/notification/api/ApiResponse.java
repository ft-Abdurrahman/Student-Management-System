package com.sms.notification.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiResponse<T>(String status, String message, OffsetDateTime timestamp, T data, List<ApiError> errors) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>("SUCCESS", "OK", OffsetDateTime.now(), data, null); }
    public static <T> ApiResponse<T> ok(String msg, T data) { return new ApiResponse<>("SUCCESS", msg, OffsetDateTime.now(), data, null); }
    public static <T> ApiResponse<T> error(String msg, List<ApiError> errs) { return new ApiResponse<>("ERROR", msg, OffsetDateTime.now(), null, errs); }
    public record ApiError(String field, String message) {}
}
