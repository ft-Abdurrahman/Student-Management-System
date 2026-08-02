package com.sms.result.repository;
import com.sms.result.entity.ResultRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ResultRepository extends JpaRepository<ResultRecord, Long> {
    List<ResultRecord> findByStudentId(Long studentId);
    Optional<ResultRecord> findByStudentIdAndExamIdAndSubjectId(Long studentId, Long examId, Long subjectId);
}
