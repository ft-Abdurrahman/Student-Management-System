package com.sms.result.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiResponse<T>(String status, String message, OffsetDateTime timestamp, T data, List<ApiError> errors) {
    public static <T> ApiResponse<T> ok(T d) { return new ApiResponse<>("SUCCESS","OK",OffsetDateTime.now(),d,null); }
    public static <T> ApiResponse<T> ok(String m, T d) { return new ApiResponse<>("SUCCESS",m,OffsetDateTime.now(),d,null); }
    public static <T> ApiResponse<T> error(String m, List<ApiError> e) { return new ApiResponse<>("ERROR",m,OffsetDateTime.now(),null,e); }
    public record ApiError(String field, String message) {}
}
