package com.sms.fee.repository;
import com.sms.fee.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> { List<FeeStructure> findByCourseIdAndSemester(Long courseId, Integer semester); }
