package com.sms.course.web;
import com.sms.course.api.ApiResponse;
import com.sms.course.entity.Course;
import com.sms.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/courses") @RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courses;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Page<Course>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size){ return ApiResponse.ok(courses.findAll(PageRequest.of(page,size, Sort.by("name")))); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Course> get(@PathVariable Long id){ return ApiResponse.ok(find(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Course> create(@RequestBody Course c){ c.setId(null); return ApiResponse.ok("created", courses.save(c)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Course> update(@PathVariable Long id, @RequestBody Course c){ Course x=find(id); if(c.getCode()!=null)x.setCode(c.getCode()); if(c.getName()!=null)x.setName(c.getName()); if(c.getCredits()!=null)x.setCredits(c.getCredits()); if(c.getDurationSemesters()!=null)x.setDurationSemesters(c.getDurationSemesters()); if(c.getDepartmentId()!=null)x.setDepartmentId(c.getDepartmentId()); if(c.getStatus()!=null)x.setStatus(c.getStatus()); return ApiResponse.ok("updated", courses.save(x)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){ courses.delete(find(id)); return ApiResponse.ok("deleted", null); }
    private Course find(Long id){ return courses.findById(id).orElseThrow(() -> new IllegalArgumentException("Course not found: "+id)); }
}
