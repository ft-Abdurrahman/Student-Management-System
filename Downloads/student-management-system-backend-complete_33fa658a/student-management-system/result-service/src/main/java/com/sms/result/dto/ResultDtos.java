package com.sms.result.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class ResultDtos {
    public record MarksReq(@NotNull Long studentId, @NotNull Long examId, @NotNull Long subjectId, @NotNull BigDecimal marksObtained, @NotNull BigDecimal maxMarks) {}
    public record GpaResponse(Long studentId, double gpa, double cgpa, long subjects) {}
}
