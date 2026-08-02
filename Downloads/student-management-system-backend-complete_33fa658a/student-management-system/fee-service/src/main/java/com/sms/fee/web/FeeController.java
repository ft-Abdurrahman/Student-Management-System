package com.sms.fee.web;

import com.sms.fee.api.ApiResponse;
import com.sms.fee.dto.FeeDtos.PayReq;
import com.sms.fee.entity.*;
import com.sms.fee.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeeController {
    private final FeeStructureRepository structures;
    private final StudentFeeRepository studentFees;
    private final FeeReceiptRepository receipts;

    @GetMapping("/structure")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<List<FeeStructure>> structure(@RequestParam Long courseId, @RequestParam Integer semester) {
        return ApiResponse.ok(structures.findByCourseIdAndSemester(courseId, semester));
    }

    @PostMapping("/structure")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<FeeStructure> createStructure(@RequestBody FeeStructure s) { s.setId(null); return ApiResponse.ok("created", structures.save(s)); }

    @PostMapping("/student-fees")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<StudentFee> createStudentFee(@RequestBody StudentFee f) { f.setId(null); return ApiResponse.ok("created", studentFees.save(f)); }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<List<StudentFee>> student(@PathVariable Long id) { return ApiResponse.ok(studentFees.findByStudentId(id)); }

    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','STUDENT')")
    public ApiResponse<Map<String, Object>> pay(@Valid @RequestBody PayReq req) {
        FeeReceipt receipt = FeeReceipt.builder().studentId(req.studentId()).studentFeeId(req.studentFeeId()).amount(req.amount()).method(req.method()).reference(req.reference()).build();
        receipt = receipts.save(receipt);
        if (req.studentFeeId() != null) {
            studentFees.findById(req.studentFeeId()).ifPresent(f -> {
                f.setPaid((f.getPaid() == null ? BigDecimal.ZERO : f.getPaid()).add(req.amount()));
                studentFees.save(f);
            });
        }
        return ApiResponse.ok("paid", Map.of("receiptId", receipt.getId(), "redirectUrl", "/api/v1/fees/receipts/" + receipt.getId() + ".pdf"));
    }

    @GetMapping("/student/{id}/receipts")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ApiResponse<List<FeeReceipt>> receipts(@PathVariable Long id) { return ApiResponse.ok(receipts.findByStudentIdOrderByPaidAtDesc(id)); }

    @GetMapping("/receipts/{id}.pdf")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<byte[]> receiptPdf(@PathVariable Long id) {
        FeeReceipt r = receipts.findById(id).orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + id));
        String text = "Fee Receipt #" + r.getId() + "\nStudent: " + r.getStudentId() + "\nAmount: " + r.getAmount() + "\nPaid at: " + r.getPaidAt();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(minimalPdf(text));
    }

    private byte[] minimalPdf(String text) {
        String safe = text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("\n", "\\n");
        String stream = "BT /F1 12 Tf 50 790 Td (" + safe + ") Tj ET";
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
