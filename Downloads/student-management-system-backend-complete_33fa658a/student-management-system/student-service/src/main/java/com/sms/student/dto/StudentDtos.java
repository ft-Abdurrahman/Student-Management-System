package com.sms.student.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class StudentDtos {
    public record CreateReq(
            @NotBlank String rollNumber,
            @NotBlank String admissionNumber,
            @NotBlank String firstName,
            String lastName,
            LocalDate dob,
            @Pattern(regexp = "M|F|O") String gender,
            @Email @NotBlank String email,
            String phone,
            String bloodGroup,
            String photoUrl,
            Long departmentId,
            Long courseId,
            @Min(1) @Max(12) Integer semester,
            ParentReq parent,
            AddressReq address
    ) {}

    public record UpdateReq(
            String firstName, String lastName, LocalDate dob, String gender,
            String email, String phone, String bloodGroup, String photoUrl,
            Long departmentId, Long courseId, Integer semester, String status,
            ParentReq parent, AddressReq address
    ) {}

    public record ParentReq(String fatherName, String fatherPhone, String motherName,
                            String motherPhone, String guardianName, String guardianPhone) {}
    public record AddressReq(String line1, String line2, String city, String state,
                             String country, String postalCode) {}

    public record View(Long id, String rollNumber, String admissionNumber, String firstName,
                       String lastName, LocalDate dob, String gender, String email, String phone,
                       String bloodGroup, String photoUrl, Long departmentId, Long courseId,
                       Integer semester, String status,
                       ParentReq parent, AddressReq address) {}
}
