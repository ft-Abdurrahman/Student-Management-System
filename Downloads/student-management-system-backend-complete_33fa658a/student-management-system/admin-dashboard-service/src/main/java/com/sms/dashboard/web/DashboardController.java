package com.sms.dashboard.web;
import com.sms.dashboard.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/v1/dashboard") @RequiredArgsConstructor
public class DashboardController {
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<Map<String,Object>> summary(){
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("totalStudents",0);
        m.put("totalTeachers",0);
        m.put("departments",0);
        m.put("courses",0);
        m.put("todayAttendance",0.0);
        m.put("revenueMonth",0);
        m.put("pendingFees",0);
        m.put("upcomingExams",0);
        m.put("recentAdmissions", List.of());
        return ApiResponse.ok(m); }
    @GetMapping("/charts/admissions")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<Map<String,Object>> admissions(@RequestParam(defaultValue="12M") String range){
        return ApiResponse.ok(chart(range, List.of(12,18,21,30,25,33,29,41,38,44,52,60))); }
    @GetMapping("/charts/attendance")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<Map<String,Object>> attendance(@RequestParam(defaultValue="30D") String range){
        return ApiResponse.ok(chart(range, List.of(91,88,93,94,90,89,92,95,94,93))); }
    @GetMapping("/charts/revenue")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<Map<String,Object>> revenue(@RequestParam(defaultValue="12M") String range){
        return ApiResponse.ok(chart(range, List.of(80000,95000,110000,125000,132000,140000,138000,142000,150000,158000,162000,128000))); }
    @GetMapping("/recent-admissions")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<List<Map<String,Object>>> recent(@RequestParam(defaultValue="10") int limit){
        return ApiResponse.ok(java.util.stream.IntStream.range(0, Math.max(0, limit)).mapToObj(i -> {
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("name", "Student "+(i+1));
            row.put("course", "Course");
            row.put("date", LocalDate.now().minusDays(i).toString()); return row; }).toList()); }
    private Map<String,Object> chart(String range, List<Integer> values){
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("range", range); m.put("values", values); return m; }
}
