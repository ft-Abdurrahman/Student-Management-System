package com.sms.student.service;

import com.sms.student.dto.StudentDtos.*;
import com.sms.student.entity.Student;
import com.sms.student.mapper.StudentMapper;
import com.sms.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repo;

    @Transactional(readOnly = true)
    public Page<View> list(String q, Long deptId, Long courseId, Integer semester, String status, Pageable p) {
        return repo.findAll(StudentSpecs.filter(q, deptId, courseId, semester, status), p).map(StudentMapper::toView);
    }

    @Transactional(readOnly = true)
    public View get(Long id) { return StudentMapper.toView(find(id)); }

    @Transactional
    public View create(CreateReq req) { return StudentMapper.toView(repo.save(StudentMapper.fromCreate(req))); }

    @Transactional
    public View update(Long id, UpdateReq req) {
        Student s = find(id);
        StudentMapper.applyUpdate(s, req);
        s.setUpdatedAt(LocalDateTime.now());
        return StudentMapper.toView(repo.save(s));
    }

    @Transactional
    public void delete(Long id) {
        Student s = find(id);
        s.setStatus("INACTIVE"); s.setUpdatedAt(LocalDateTime.now()); // soft delete
        repo.save(s);
    }

    public Student find(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
    }
}
