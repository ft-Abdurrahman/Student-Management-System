package com.sms.teacher.repository;
import com.sms.teacher.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    List<TeacherSubject> findByTeacherId(Long teacherId);
    Optional<TeacherSubject> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
}
