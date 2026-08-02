package com.sms.report.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="report_jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportJob {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=40) private String type;
    @Lob @Column(name="filters_json") private String filtersJson;
    @Column(nullable=false, length=10) private String format;
    @Column(nullable=false, length=20) private String status;
    @Column(name="file_name") private String fileName;
    @Lob @Column(name="content", columnDefinition="LONGBLOB") private byte[] content;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
}
