package com.sms.report.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.report.api.ApiResponse;
import com.sms.report.dto.ReportDtos.*;
import com.sms.report.entity.ReportJob;
import com.sms.report.repository.ReportJobRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportJobRepository reports;
    private final ObjectMapper mapper;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ApiResponse<GenerateResp> generate(@Valid @RequestBody GenerateReq req) throws Exception {
        String filters = mapper.writeValueAsString(req.filters() == null ? Map.of() : req.filters());
        String fmt = req.format().toUpperCase();
        byte[] content = "PDF".equals(fmt)
            ? minimalPdf("SMS " + req.type() + " report\nFilters: " + filters)
            : ("type,filters\n" + req.type() + ",\"" + filters.replace("\"", "'") + "\"\n").getBytes(StandardCharsets.UTF_8);
        String name = req.type().toLowerCase() + "-report." + ("PDF".equals(fmt) ? "pdf" : "csv");
        ReportJob job = reports.save(ReportJob.builder().type(req.type()).filtersJson(filters).format(fmt).status("READY").fileName(name).content(content).build());
        return ApiResponse.ok("generated", new GenerateResp(job.getId(), job.getType(), job.getFormat(), job.getStatus(), job.getFileName()));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER')")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        ReportJob r = reports.findById(id).orElseThrow(() -> new IllegalArgumentException("Report not found: " + id));
        MediaType mt = "PDF".equals(r.getFormat()) ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + r.getFileName()).contentType(mt).body(r.getContent());
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
