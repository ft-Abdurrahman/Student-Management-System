package com.sms.exam.repository;
import com.sms.exam.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> { List<ExamSchedule> findByExamIdOrderByExamDateAscStartTimeAsc(Long examId); }
