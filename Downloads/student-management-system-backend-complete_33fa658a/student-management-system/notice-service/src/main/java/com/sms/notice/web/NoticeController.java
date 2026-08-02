package com.sms.notice.web;
import com.sms.notice.api.ApiResponse;
import com.sms.notice.entity.Notice;
import com.sms.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/notices") @RequiredArgsConstructor
public class NoticeController {
    private final NoticeRepository notices;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Page<Notice>> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size, @RequestParam(required=false) String audience, @RequestParam(required=false) Long departmentId, @RequestParam(required=false) Boolean pinned){ return ApiResponse.ok(notices.findAll(spec(audience,departmentId,pinned), PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"pinned").and(Sort.by(Sort.Direction.DESC,"publishAt"))))); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<Notice> get(@PathVariable Long id){ return ApiResponse.ok(find(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<Notice> create(@RequestBody Notice n){ n.setId(null); return ApiResponse.ok("created", notices.save(n)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<Notice> update(@PathVariable Long id, @RequestBody Notice n){ Notice x=find(id); if(n.getTitle()!=null)x.setTitle(n.getTitle()); if(n.getBody()!=null)x.setBody(n.getBody()); if(n.getAudience()!=null)x.setAudience(n.getAudience()); if(n.getDepartmentId()!=null)x.setDepartmentId(n.getDepartmentId()); x.setPinned(n.isPinned()); if(n.getAuthor()!=null)x.setAuthor(n.getAuthor()); if(n.getPublishAt()!=null)x.setPublishAt(n.getPublishAt()); if(n.getExpiresAt()!=null)x.setExpiresAt(n.getExpiresAt()); if(n.getStatus()!=null)x.setStatus(n.getStatus()); return ApiResponse.ok("updated", notices.save(x)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<Void> delete(@PathVariable Long id){ notices.delete(find(id)); return ApiResponse.ok("deleted", null); }
    @PostMapping("/{id}/pin") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<Notice> pin(@PathVariable Long id){ Notice n=find(id); n.setPinned(true); return ApiResponse.ok("pinned", notices.save(n)); }
    @DeleteMapping("/{id}/pin") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<Notice> unpin(@PathVariable Long id){ Notice n=find(id); n.setPinned(false); return ApiResponse.ok("unpinned", notices.save(n)); }
    private Notice find(Long id){ return notices.findById(id).orElseThrow(() -> new IllegalArgumentException("Notice not found: "+id)); }
    private Specification<Notice> spec(String audience, Long departmentId, Boolean pinned){ return (root, query, cb) -> { List<jakarta.persistence.criteria.Predicate> p=new ArrayList<>(); if(audience!=null&&!audience.isBlank())p.add(cb.equal(root.get("audience"), audience)); if(departmentId!=null)p.add(cb.equal(root.get("departmentId"), departmentId)); if(pinned!=null)p.add(cb.equal(root.get("pinned"), pinned)); return cb.and(p.toArray(new jakarta.persistence.criteria.Predicate[0])); }; }
}
