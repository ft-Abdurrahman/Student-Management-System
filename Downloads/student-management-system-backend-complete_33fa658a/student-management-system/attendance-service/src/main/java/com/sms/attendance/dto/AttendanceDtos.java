package com.sms.attendance.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public class AttendanceDtos {
    public record MarkReq(@NotNull Long studentId, @NotNull Long subjectId, @NotNull LocalDate date, @NotBlank String status, Long markedByTeacherId) {}
    public record LeaveReq(@NotNull Long studentId, @NotNull LocalDate fromDate, @NotNull LocalDate toDate, String reason) {}
    public record Percentage(long total, long present, double percentage) {}
    public record MonthlyReportRow(Long studentId, long total, long present, long absent, long late, long leave, double percentage) {}
}
