package com.sms.attendance.web;
import com.sms.attendance.api.ApiResponse;
import com.sms.attendance.dto.AttendanceDtos.*;
import com.sms.attendance.entity.*;
import com.sms.attendance.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
@RestController @RequestMapping("/api/v1/attendance") @RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceRepository attendance; private final LeaveRequestRepository leaves;
    @PostMapping("/mark") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<List<Attendance>> mark(@Valid @RequestBody List<MarkReq> rows){ return ApiResponse.ok("marked", rows.stream().map(this::upsert).toList()); }
    @GetMapping("/student/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<List<Attendance>> student(@PathVariable Long id, @RequestParam(required=false) LocalDate from, @RequestParam(required=false) LocalDate to, @RequestParam(required=false) Long subjectId){ List<Attendance> rows=(from!=null&&to!=null)?attendance.findByStudentIdAndAttendanceDateBetween(id, from, to):attendance.findByStudentId(id); if(subjectId!=null) rows=rows.stream().filter(a -> Objects.equals(a.getSubjectId(), subjectId)).toList(); return ApiResponse.ok(rows); }
    @GetMapping("/student/{id}/percentage") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Percentage> percentage(@PathVariable Long id, @RequestParam(required=false) Long subjectId){ List<Attendance> rows=attendance.findByStudentId(id); if(subjectId!=null) rows=rows.stream().filter(a -> Objects.equals(a.getSubjectId(), subjectId)).toList(); long total=rows.size(); long present=rows.stream().filter(a -> List.of("PRESENT","LATE").contains(a.getStatus())).count(); return ApiResponse.ok(new Percentage(total, present, total==0?0.0:(present*100.0/total))); }
    @GetMapping("/report/monthly") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<List<MonthlyReportRow>> monthly(@RequestParam String month){ YearMonth ym=YearMonth.parse(month); var rows=attendance.findByAttendanceDateBetween(ym.atDay(1), ym.atEndOfMonth()); return ApiResponse.ok(rows.stream().collect(Collectors.groupingBy(Attendance::getStudentId)).entrySet().stream().map(e -> row(e.getKey(), e.getValue())).toList()); }
    @PostMapping("/leave") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<LeaveRequest> leave(@Valid @RequestBody LeaveReq req){ LeaveRequest lr=LeaveRequest.builder().studentId(req.studentId()).fromDate(req.fromDate()).toDate(req.toDate()).reason(req.reason()).build(); return ApiResponse.ok("created", leaves.save(lr)); }
    @PutMapping("/leave/{id}/approve") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<LeaveRequest> approve(@PathVariable Long id, @RequestParam(required=false) Long reviewedBy){ LeaveRequest lr=findLeave(id); lr.setStatus("APPROVED"); lr.setReviewedBy(reviewedBy); lr.setReviewedAt(LocalDateTime.now()); return ApiResponse.ok("approved", leaves.save(lr)); }
    @PutMapping("/leave/{id}/reject") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<LeaveRequest> reject(@PathVariable Long id, @RequestParam(required=false) Long reviewedBy){ LeaveRequest lr=findLeave(id); lr.setStatus("REJECTED"); lr.setReviewedBy(reviewedBy); lr.setReviewedAt(LocalDateTime.now()); return ApiResponse.ok("rejected", leaves.save(lr)); }
    private Attendance upsert(MarkReq r){ Attendance a=attendance.findByStudentIdAndSubjectIdAndAttendanceDate(r.studentId(), r.subjectId(), r.date()).orElseGet(Attendance::new); a.setStudentId(r.studentId()); a.setSubjectId(r.subjectId()); a.setAttendanceDate(r.date()); a.setStatus(r.status()); a.setMarkedByTeacherId(r.markedByTeacherId()); return attendance.save(a); }
    private LeaveRequest findLeave(Long id){ return leaves.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave request not found: "+id)); }
    private MonthlyReportRow row(Long id, List<Attendance> rows){ long total=rows.size(), present=rows.stream().filter(a->"PRESENT".equals(a.getStatus())).count(), absent=rows.stream().filter(a->"ABSENT".equals(a.getStatus())).count(), late=rows.stream().filter(a->"LATE".equals(a.getStatus())).count(), leave=rows.stream().filter(a->"LEAVE".equals(a.getStatus())).count(); return new MonthlyReportRow(id,total,present,absent,late,leave,total==0?0.0:((present+late)*100.0/total)); }
}
