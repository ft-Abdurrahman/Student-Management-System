package com.sms.exam.web;
import com.sms.exam.api.ApiResponse;
import com.sms.exam.entity.*;
import com.sms.exam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/exams") @RequiredArgsConstructor
public class ExamController {
    private final ExamRepository exams; private final ExamScheduleRepository schedules;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Page<Exam>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size, @RequestParam(required=false) Long courseId, @RequestParam(required=false) Integer semester){ return ApiResponse.ok(exams.findAll(spec(courseId,semester), PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"startDate")))); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Exam> get(@PathVariable Long id){ return ApiResponse.ok(find(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Exam> create(@RequestBody Exam e){ e.setId(null); return ApiResponse.ok("created", exams.save(e)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Exam> update(@PathVariable Long id, @RequestBody Exam e){ Exam x=find(id); if(e.getName()!=null)x.setName(e.getName()); if(e.getType()!=null)x.setType(e.getType()); if(e.getCourseId()!=null)x.setCourseId(e.getCourseId()); if(e.getSemester()!=null)x.setSemester(e.getSemester()); if(e.getStartDate()!=null)x.setStartDate(e.getStartDate()); if(e.getEndDate()!=null)x.setEndDate(e.getEndDate()); if(e.getStatus()!=null)x.setStatus(e.getStatus()); return ApiResponse.ok("updated", exams.save(x)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){ exams.delete(find(id)); return ApiResponse.ok("deleted", null); }
    @PostMapping("/{id}/schedule") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<ExamSchedule> schedule(@PathVariable Long id, @RequestBody ExamSchedule s){ find(id); s.setId(null); s.setExamId(id); return ApiResponse.ok("scheduled", schedules.save(s)); }
    @GetMapping("/{id}/schedule") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<List<ExamSchedule>> schedule(@PathVariable Long id){ find(id); return ApiResponse.ok(schedules.findByExamIdOrderByExamDateAscStartTimeAsc(id)); }
    private Exam find(Long id){ return exams.findById(id).orElseThrow(() -> new IllegalArgumentException("Exam not found: "+id)); }
    private Specification<Exam> spec(Long courseId, Integer semester){ return (root, query, cb) -> { List<jakarta.persistence.criteria.Predicate> p=new ArrayList<>(); if(courseId!=null)p.add(cb.equal(root.get("courseId"),courseId)); if(semester!=null)p.add(cb.equal(root.get("semester"),semester)); return cb.and(p.toArray(new jakarta.persistence.criteria.Predicate[0])); }; }
}
