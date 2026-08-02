package com.sms.subject.dto;
import jakarta.validation.constraints.NotNull;
public class SubjectDtos { public record AssignTeacherReq(@NotNull Long teacherId) {} }
