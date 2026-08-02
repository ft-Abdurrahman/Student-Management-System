package com.sms.course.repository;
import com.sms.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CourseRepository extends JpaRepository<Course, Long> { List<Course> findByDepartmentId(Long departmentId); }
