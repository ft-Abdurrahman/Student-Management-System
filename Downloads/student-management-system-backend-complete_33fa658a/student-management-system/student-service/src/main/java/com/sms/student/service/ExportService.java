package com.sms.student.service;

import com.sms.student.entity.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    /** Build an Excel workbook (.xlsx) with the given students. */
    public byte[] toExcel(List<Student> rows) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Students");
            CellStyle head = wb.createCellStyle();
            Font f = wb.createFont(); f.setBold(true); head.setFont(f);
            String[] cols = {"Roll No","Admission No","First","Last","Email","Phone","Dept","Course","Sem","Status"};
            Row h = sh.createRow(0);
            for (int i = 0; i < cols.length; i++) { Cell c = h.createCell(i); c.setCellValue(cols[i]); c.setCellStyle(head); }
            int r = 1;
            for (Student s : rows) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(s.getRollNumber());
                row.createCell(1).setCellValue(s.getAdmissionNumber());
                row.createCell(2).setCellValue(s.getFirstName());
                row.createCell(3).setCellValue(nz(s.getLastName()));
                row.createCell(4).setCellValue(s.getEmail());
                row.createCell(5).setCellValue(nz(s.getPhone()));
                row.createCell(6).setCellValue(s.getDepartmentId() == null ? "" : String.valueOf(s.getDepartmentId()));
                row.createCell(7).setCellValue(s.getCourseId() == null ? "" : String.valueOf(s.getCourseId()));
                row.createCell(8).setCellValue(s.getSemester() == null ? 0 : s.getSemester());
                row.createCell(9).setCellValue(s.getStatus());
            }
            for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out); return out.toByteArray();
        }
    }

    /**
     * Minimal, dependency-free PDF generator (single page, plain text).
     * Uses core PDF 1.4 syntax so no third-party runtime is required.
     * For richly styled PDFs (tables, images), swap in iText or OpenPDF here.
     */
    public byte[] toPdf(Student s) {
        StringBuilder text = new StringBuilder();
        text.append("Student Profile\n\n");
        text.append("Roll No     : ").append(s.getRollNumber()).append('\n');
        text.append("Admission # : ").append(s.getAdmissionNumber()).append('\n');
        text.append("Name        : ").append(s.getFirstName()).append(' ').append(nz(s.getLastName())).append('\n');
        text.append("Email       : ").append(s.getEmail()).append('\n');
        text.append("Phone       : ").append(nz(s.getPhone())).append('\n');
        text.append("Department  : ").append(s.getDepartmentId()).append('\n');
        text.append("Course      : ").append(s.getCourseId()).append('\n');
        text.append("Semester    : ").append(s.getSemester()).append('\n');
        text.append("Status      : ").append(s.getStatus()).append('\n');
        return buildPdf(text.toString());
    }

    private String nz(String s) { return s == null ? "" : s; }

    private byte[] buildPdf(String text) {
        String[] lines = text.split("\n");
        StringBuilder content = new StringBuilder("BT /F1 12 Tf 50 780 Td 14 TL\n");
        for (String line : lines) {
            content.append('(').append(line.replace("\\","\\\\").replace("(","\\(").replace(")","\\)")).append(") Tj T*\n");
        }
        content.append("ET");
        String stream = content.toString();
        String objs =
            "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
            "2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n" +
            "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n" +
            "4 0 obj<</Length " + stream.length() + ">>stream\n" + stream + "\nendstream\nendobj\n" +
            "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n";
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n" + objs);
        int xref = pdf.length();
        pdf.append("xref\n0 6\n0000000000 65535 f \n");
        int[] offsets = new int[6]; int cur = "%PDF-1.4\n".length();
        String[] parts = objs.split("(?<=endobj\n)");
        for (int i = 0; i < parts.length; i++) { offsets[i+1] = cur; cur += parts[i].length(); }
        for (int i = 1; i < 6; i++) pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        pdf.append("trailer<</Size 6/Root 1 0 R>>\nstartxref\n").append(xref).append("\n%%EOF");
        return pdf.toString().getBytes();
    }
}
