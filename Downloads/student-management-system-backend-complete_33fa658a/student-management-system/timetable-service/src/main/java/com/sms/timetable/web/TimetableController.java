package com.sms.timetable.web;
import com.sms.timetable.api.ApiResponse;
import com.sms.timetable.entity.TimetableSlot;
import com.sms.timetable.repository.TimetableSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/timetable") @RequiredArgsConstructor
public class TimetableController {
    private final TimetableSlotRepository slots;
    @GetMapping("/student/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<List<TimetableSlot>> student(@PathVariable Long id, @RequestParam(required=false) Long courseId, @RequestParam(required=false) Integer semester){ if(courseId!=null && semester!=null) return ApiResponse.ok(slots.findByCourseIdAndSemesterOrderByDayOfWeekAscStartTimeAsc(courseId, semester)); return ApiResponse.ok(slots.findAll(Sort.by("dayOfWeek","startTime"))); }
    @GetMapping("/teacher/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')") public ApiResponse<List<TimetableSlot>> teacher(@PathVariable Long id){ return ApiResponse.ok(slots.findByTeacherIdOrderByDayOfWeekAscStartTimeAsc(id)); }
    @GetMapping("/slots") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')") public ApiResponse<List<TimetableSlot>> list(){ return ApiResponse.ok(slots.findAll(Sort.by("dayOfWeek","startTime"))); }
    @PostMapping("/slots") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<TimetableSlot> create(@RequestBody TimetableSlot s){ s.setId(null); return ApiResponse.ok("created", slots.save(s)); }
    @PutMapping("/slots/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<TimetableSlot> update(@PathVariable Long id, @RequestBody TimetableSlot s){ TimetableSlot x=find(id); if(s.getCourseId()!=null)x.setCourseId(s.getCourseId()); if(s.getSemester()!=null)x.setSemester(s.getSemester()); if(s.getSubjectId()!=null)x.setSubjectId(s.getSubjectId()); if(s.getTeacherId()!=null)x.setTeacherId(s.getTeacherId()); if(s.getDayOfWeek()!=null)x.setDayOfWeek(s.getDayOfWeek()); if(s.getStartTime()!=null)x.setStartTime(s.getStartTime()); if(s.getEndTime()!=null)x.setEndTime(s.getEndTime()); if(s.getRoom()!=null)x.setRoom(s.getRoom()); if(s.getStatus()!=null)x.setStatus(s.getStatus()); return ApiResponse.ok("updated", slots.save(x)); }
    @DeleteMapping("/slots/{id}") @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')") public ApiResponse<Void> delete(@PathVariable Long id){ slots.delete(find(id)); return ApiResponse.ok("deleted", null); }
    private TimetableSlot find(Long id){ return slots.findById(id).orElseThrow(() -> new IllegalArgumentException("Slot not found: "+id)); }
}
