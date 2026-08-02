package com.sms.student.web;

import com.sms.student.api.ApiResponse;
import com.sms.student.dto.StudentDtos.*;
import com.sms.student.entity.Student;
import com.sms.student.repository.StudentRepository;
import com.sms.student.service.ExportService;
import com.sms.student.service.StudentService;
import com.sms.student.service.StudentSpecs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService students;
    private final ExportService exports;
    private final StudentRepository repo;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<Page<View>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long department,
            @RequestParam(required = false) Long course,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String status) {
        String[] s = sort.split(",");
        Sort srt = s.length > 1 ? Sort.by(Sort.Direction.fromString(s[1]), s[0]) : Sort.by(s[0]);
        return ApiResponse.ok(students.list(q, department, course, semester, status, PageRequest.of(page, size, srt)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<View> get(@PathVariable Long id) { return ApiResponse.ok(students.get(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<View> create(@Valid @RequestBody CreateReq req) {
        return ApiResponse.ok("created", students.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<View> update(@PathVariable Long id, @Valid @RequestBody UpdateReq req) {
        return ApiResponse.ok("updated", students.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) { students.delete(id); return ApiResponse.ok("deleted", null); }

    @GetMapping("/export.xlsx")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<byte[]> excel(@RequestParam(required = false) String q,
                                        @RequestParam(required = false) Long department,
                                        @RequestParam(required = false) Long course,
                                        @RequestParam(required = false) Integer semester,
                                        @RequestParam(required = false) String status) throws Exception {
        List<Student> rows = repo.findAll(StudentSpecs.filter(q, department, course, semester, status));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exports.toExcel(rows));
    }

    @GetMapping("/{id}/export.pdf")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        Student s = students.find(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(exports.toPdf(s));
    }
}
