package com.sms.teacher.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class TeacherDtos {
    public record CreateReq(@NotBlank String employeeCode, @NotBlank String firstName, String lastName,
        @Email @NotBlank String email, String phone, Long departmentId, String qualification,
        Integer experienceYears, BigDecimal salary, String status) {}
    public record UpdateReq(String employeeCode, String firstName, String lastName, String email, String phone,
        Long departmentId, String qualification, Integer experienceYears, BigDecimal salary, String status) {}
    public record View(Long id, String employeeCode, String firstName, String lastName, String fullName,
        String email, String phone, Long departmentId, String qualification, Integer experienceYears,
        BigDecimal salary, String status) {}
    public record SubjectAssignmentReq(@NotNull Long subjectId) {}
}
