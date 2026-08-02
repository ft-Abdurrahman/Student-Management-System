package com.sms.teacher.web;

import com.sms.teacher.api.ApiResponse;
import com.sms.teacher.dto.TeacherDtos.*;
import com.sms.teacher.entity.Teacher;
import com.sms.teacher.entity.TeacherSubject;
import com.sms.teacher.repository.TeacherRepository;
import com.sms.teacher.repository.TeacherSubjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherRepository teachers;
    private final TeacherSubjectRepository subjects;

    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<Page<View>> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort, @RequestParam(required = false) String q,
            @RequestParam(required = false) Long departmentId, @RequestParam(required = false) String status) {
        return ApiResponse.ok(teachers.findAll(spec(q, departmentId, status), PageRequest.of(page, size, parseSort(sort))).map(this::view));
    }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<View> get(@PathVariable Long id) { return ApiResponse.ok(view(find(id))); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<View> create(@Valid @RequestBody CreateReq req) { Teacher t = new Teacher(); apply(t, req); return ApiResponse.ok("created", view(teachers.save(t))); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<View> update(@PathVariable Long id, @Valid @RequestBody UpdateReq req) { Teacher t = find(id); apply(t, req); return ApiResponse.ok("updated", view(teachers.save(t))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) { Teacher t = find(id); t.setStatus("INACTIVE"); teachers.save(t); return ApiResponse.ok("deleted", null); }
    @GetMapping("/{id}/subjects") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<List<Long>> assignedSubjects(@PathVariable Long id) { find(id); return ApiResponse.ok(subjects.findByTeacherId(id).stream().map(TeacherSubject::getSubjectId).toList()); }
    @PostMapping("/{id}/subjects") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<List<Long>> assignSubject(@PathVariable Long id, @Valid @RequestBody SubjectAssignmentReq req) {
        find(id); subjects.findByTeacherIdAndSubjectId(id, req.subjectId()).orElseGet(() -> subjects.save(TeacherSubject.builder().teacherId(id).subjectId(req.subjectId()).build()));
        return assignedSubjects(id);
    }
    @DeleteMapping("/{id}/subjects/{subjectId}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<Void> unassignSubject(@PathVariable Long id, @PathVariable Long subjectId) { subjects.findByTeacherIdAndSubjectId(id, subjectId).ifPresent(subjects::delete); return ApiResponse.ok("unassigned", null); }
    @GetMapping("/{id}/timetable") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<Map<String,Object>> timetable(@PathVariable Long id) { find(id); return ApiResponse.ok(Map.of("teacherId", id, "message", "Use timetable-service /api/v1/timetable/teacher/" + id)); }

    private Teacher find(Long id) { return teachers.findById(id).orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id)); }
    private Sort parseSort(String sort) { String[] s = sort.split(","); return s.length > 1 ? Sort.by(Sort.Direction.fromString(s[1]), s[0]) : Sort.by(s[0]); }
    private Specification<Teacher> spec(String q, Long dept, String status) { return (root, query, cb) -> {
        List<jakarta.persistence.criteria.Predicate> p = new ArrayList<>();
        if (q != null && !q.isBlank()) { String like = "%" + q.toLowerCase() + "%"; p.add(cb.or(cb.like(cb.lower(root.get("firstName")), like), cb.like(cb.lower(root.get("lastName")), like), cb.like(cb.lower(root.get("email")), like), cb.like(cb.lower(root.get("employeeCode")), like))); }
        if (dept != null) p.add(cb.equal(root.get("departmentId"), dept));
        if (status != null && !status.isBlank()) p.add(cb.equal(root.get("status"), status));
        return cb.and(p.toArray(new jakarta.persistence.criteria.Predicate[0])); };
    }
    private View view(Teacher t) { String ln = t.getLastName() == null ? "" : " " + t.getLastName(); return new View(t.getId(), t.getEmployeeCode(), t.getFirstName(), t.getLastName(), t.getFirstName() + ln, t.getEmail(), t.getPhone(), t.getDepartmentId(), t.getQualification(), t.getExperienceYears(), t.getSalary(), t.getStatus()); }
    private void apply(Teacher t, CreateReq r) { t.setEmployeeCode(r.employeeCode()); t.setFirstName(r.firstName()); t.setLastName(r.lastName()); t.setEmail(r.email()); t.setPhone(r.phone()); t.setDepartmentId(r.departmentId()); t.setQualification(r.qualification()); t.setExperienceYears(r.experienceYears()); t.setSalary(r.salary()); if (r.status()!=null) t.setStatus(r.status()); }
    private void apply(Teacher t, UpdateReq r) { if(r.employeeCode()!=null)t.setEmployeeCode(r.employeeCode()); if(r.firstName()!=null)t.setFirstName(r.firstName()); if(r.lastName()!=null)t.setLastName(r.lastName()); if(r.email()!=null)t.setEmail(r.email()); if(r.phone()!=null)t.setPhone(r.phone()); if(r.departmentId()!=null)t.setDepartmentId(r.departmentId()); if(r.qualification()!=null)t.setQualification(r.qualification()); if(r.experienceYears()!=null)t.setExperienceYears(r.experienceYears()); if(r.salary()!=null)t.setSalary(r.salary()); if(r.status()!=null)t.setStatus(r.status()); }
}
