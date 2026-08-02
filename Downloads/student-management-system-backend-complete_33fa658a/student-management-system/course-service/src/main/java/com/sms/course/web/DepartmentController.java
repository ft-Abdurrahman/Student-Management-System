package com.sms.course.web;
import com.sms.course.api.ApiResponse;
import com.sms.course.entity.*;
import com.sms.course.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/departments") @RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departments; private final CourseRepository courses;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Page<Department>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size){ return ApiResponse.ok(departments.findAll(PageRequest.of(page,size, Sort.by("name")))); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Department> get(@PathVariable Long id){ return ApiResponse.ok(find(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Department> create(@RequestBody Department d){ d.setId(null); return ApiResponse.ok("created", departments.save(d)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Department> update(@PathVariable Long id, @RequestBody Department d){ Department x=find(id); if(d.getCode()!=null)x.setCode(d.getCode()); if(d.getName()!=null)x.setName(d.getName()); if(d.getHeadTeacherId()!=null)x.setHeadTeacherId(d.getHeadTeacherId()); if(d.getStatus()!=null)x.setStatus(d.getStatus()); return ApiResponse.ok("updated", departments.save(x)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){ departments.delete(find(id)); return ApiResponse.ok("deleted", null); }
    @GetMapping("/{id}/courses") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<List<Course>> courses(@PathVariable Long id){ find(id); return ApiResponse.ok(courses.findByDepartmentId(id)); }
    private Department find(Long id){ return departments.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found: "+id)); }
}
