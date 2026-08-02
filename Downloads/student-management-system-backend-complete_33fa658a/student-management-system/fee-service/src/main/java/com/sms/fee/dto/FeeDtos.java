package com.sms.fee.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class FeeDtos { public record PayReq(@NotNull Long studentId, Long studentFeeId, @NotNull BigDecimal amount, String method, String reference) {} }
