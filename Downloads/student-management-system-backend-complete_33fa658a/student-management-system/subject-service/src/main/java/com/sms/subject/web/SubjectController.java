package com.sms.subject.web;
import com.sms.subject.api.ApiResponse;
import com.sms.subject.dto.SubjectDtos.AssignTeacherReq;
import com.sms.subject.entity.Subject;
import com.sms.subject.repository.SubjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/subjects") @RequiredArgsConstructor
public class SubjectController {
    private final SubjectRepository subjects;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<Page<Subject>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size, @RequestParam(required=false) Long courseId, @RequestParam(required=false) Integer semester, @RequestParam(required=false) String q){ return ApiResponse.ok(subjects.findAll(spec(courseId, semester, q), PageRequest.of(page,size, Sort.by("code")))); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Subject> get(@PathVariable Long id){ return ApiResponse.ok(find(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Subject> create(@RequestBody Subject s){ s.setId(null); return ApiResponse.ok("created", subjects.save(s)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Subject> update(@PathVariable Long id, @RequestBody Subject s){ Subject x=find(id); if(s.getCode()!=null)x.setCode(s.getCode()); if(s.getName()!=null)x.setName(s.getName()); if(s.getCredits()!=null)x.setCredits(s.getCredits()); if(s.getSemester()!=null)x.setSemester(s.getSemester()); if(s.getCourseId()!=null)x.setCourseId(s.getCourseId()); if(s.getTeacherId()!=null)x.setTeacherId(s.getTeacherId()); if(s.getStatus()!=null)x.setStatus(s.getStatus()); return ApiResponse.ok("updated", subjects.save(x)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){ subjects.delete(find(id)); return ApiResponse.ok("deleted", null); }
    @PostMapping("/{id}/assign-teacher") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Subject> assign(@PathVariable Long id, @Valid @RequestBody AssignTeacherReq req){ Subject s=find(id); s.setTeacherId(req.teacherId()); return ApiResponse.ok("assigned", subjects.save(s)); }
    private Subject find(Long id){ return subjects.findById(id).orElseThrow(() -> new IllegalArgumentException("Subject not found: "+id)); }
    private Specification<Subject> spec(Long courseId, Integer semester, String q){ return (root, query, cb) -> { List<jakarta.persistence.criteria.Predicate> p=new ArrayList<>(); if(courseId!=null)p.add(cb.equal(root.get("courseId"), courseId)); if(semester!=null)p.add(cb.equal(root.get("semester"), semester)); if(q!=null&&!q.isBlank()){String like="%"+q.toLowerCase()+"%"; p.add(cb.or(cb.like(cb.lower(root.get("name")), like), cb.like(cb.lower(root.get("code")), like)));} return cb.and(p.toArray(new jakarta.persistence.criteria.Predicate[0])); }; }
}
