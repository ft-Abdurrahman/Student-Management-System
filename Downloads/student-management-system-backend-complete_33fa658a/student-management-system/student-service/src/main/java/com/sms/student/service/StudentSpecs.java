package com.sms.student.service;

import com.sms.student.entity.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StudentSpecs {
    public static Specification<Student> filter(String q, Long deptId, Long courseId,
                                                Integer semester, String status) {
        return (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), like),
                        cb.like(cb.lower(root.get("lastName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("rollNumber")), like),
                        cb.like(cb.lower(root.get("admissionNumber")), like)));
            }
            if (deptId != null) ps.add(cb.equal(root.get("departmentId"), deptId));
            if (courseId != null) ps.add(cb.equal(root.get("courseId"), courseId));
            if (semester != null) ps.add(cb.equal(root.get("semester"), semester));
            if (status != null && !status.isBlank()) ps.add(cb.equal(root.get("status"), status));
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
