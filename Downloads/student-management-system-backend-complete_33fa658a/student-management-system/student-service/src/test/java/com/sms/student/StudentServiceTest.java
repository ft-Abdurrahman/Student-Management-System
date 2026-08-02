package com.sms.student;

import com.sms.student.dto.StudentDtos.CreateReq;
import com.sms.student.dto.StudentDtos.ParentReq;
import com.sms.student.repository.StudentRepository;
import com.sms.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class StudentServiceTest {

    @Autowired StudentService service;
    @Autowired StudentRepository repo;

    @Test void createAndReadStudent() {
        var view = service.create(new CreateReq(
                "CSE-TEST-001","ADM-T-001","Test","User",
                null,"M","test@sms.edu","+91 9999999999",null,null,
                1L, 1L, 1,
                new ParentReq("F","111","M","222",null,null),
                null));
        assertThat(view.id()).isNotNull();
        assertThat(service.get(view.id()).firstName()).isEqualTo("Test");
    }
}
