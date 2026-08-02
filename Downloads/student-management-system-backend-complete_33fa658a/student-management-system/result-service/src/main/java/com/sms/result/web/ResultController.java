package com.sms.result.web;

import com.sms.result.api.ApiResponse;
import com.sms.result.dto.ResultDtos.*;
import com.sms.result.entity.ResultRecord;
import com.sms.result.repository.ResultRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultRepository results;

    @PostMapping("/marks")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<List<ResultRecord>> marks(@Valid @RequestBody List<MarksReq> rows) {
        return ApiResponse.ok("saved", rows.stream().map(this::upsert).toList());
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<List<ResultRecord>> student(@PathVariable Long id) {
        return ApiResponse.ok(results.findByStudentId(id));
    }

    @GetMapping("/student/{id}/gpa")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<GpaResponse> gpa(@PathVariable Long id) {
        List<ResultRecord> rows = results.findByStudentId(id);
        double avg = rows.stream().filter(r -> r.getGpa() != null).mapToDouble(r -> r.getGpa().doubleValue()).average().orElse(0.0);
        return ApiResponse.ok(new GpaResponse(id, round(avg), round(avg), rows.size()));
    }

    @GetMapping("/student/{id}/transcript.pdf")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<byte[]> transcript(@PathVariable Long id) {
        StringBuilder text = new StringBuilder("Transcript for student #").append(id).append("\n\n");
        for (ResultRecord r : results.findByStudentId(id)) {
            text.append("Exam ").append(r.getExamId())
                .append(" Subject ").append(r.getSubjectId())
                .append(" Marks ").append(r.getMarksObtained()).append("/").append(r.getMaxMarks())
                .append(" Grade ").append(r.getGrade())
                .append(" GPA ").append(r.getGpa()).append("\n");
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(minimalPdf(text.toString()));
    }

    private ResultRecord upsert(MarksReq r) {
        ResultRecord x = results.findByStudentIdAndExamIdAndSubjectId(r.studentId(), r.examId(), r.subjectId()).orElseGet(ResultRecord::new);
        x.setStudentId(r.studentId());
        x.setExamId(r.examId());
        x.setSubjectId(r.subjectId());
        x.setMarksObtained(r.marksObtained());
        x.setMaxMarks(r.maxMarks());
        double pct = r.maxMarks().compareTo(BigDecimal.ZERO) == 0 ? 0 : r.marksObtained().multiply(BigDecimal.valueOf(100)).divide(r.maxMarks(), 2, RoundingMode.HALF_UP).doubleValue();
        x.setGrade(grade(pct));
        x.setGpa(BigDecimal.valueOf(gpa(pct)));
        return results.save(x);
    }

    private String grade(double p) { return p >= 90 ? "A+" : p >= 80 ? "A" : p >= 70 ? "B+" : p >= 60 ? "B" : p >= 50 ? "C" : "F"; }
    private double gpa(double p) { return p >= 90 ? 10 : p >= 80 ? 9 : p >= 70 ? 8 : p >= 60 ? 7 : p >= 50 ? 6 : 0; }
    private double round(double d) { return Math.round(d * 100.0) / 100.0; }

    private byte[] minimalPdf(String text) {
        String safe = text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("\n", "\\n");
        String stream = "BT /F1 11 Tf 50 790 Td (" + safe + ") Tj ET";
        String pdf = "%PDF-1.4\n"
            + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
            + "2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n"
            + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 595 842]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n"
            + "4 0 obj<</Length " + stream.length() + ">>stream\n" + stream + "\nendstream endobj\n"
            + "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n"
            + "trailer<</Root 1 0 R>>\n%%EOF";
        return pdf.getBytes(StandardCharsets.UTF_8);
    }
}
