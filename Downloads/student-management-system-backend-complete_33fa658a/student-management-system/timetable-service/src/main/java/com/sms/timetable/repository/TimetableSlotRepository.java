package com.sms.timetable.repository;
import com.sms.timetable.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findByTeacherIdOrderByDayOfWeekAscStartTimeAsc(Long teacherId);
    List<TimetableSlot> findByCourseIdAndSemesterOrderByDayOfWeekAscStartTimeAsc(Long courseId, Integer semester);
}
