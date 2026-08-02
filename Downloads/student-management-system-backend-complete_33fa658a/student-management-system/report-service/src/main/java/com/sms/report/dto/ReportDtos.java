package com.sms.report.dto;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
public class ReportDtos { public record GenerateReq(@NotBlank String type, Map<String,Object> filters, @NotBlank String format) {} public record GenerateResp(Long id, String type, String format, String status, String fileName) {} }
