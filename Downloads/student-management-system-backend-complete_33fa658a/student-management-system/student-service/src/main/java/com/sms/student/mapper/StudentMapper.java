package com.sms.student.mapper;

import com.sms.student.dto.StudentDtos.*;
import com.sms.student.entity.*;

public class StudentMapper {
    public static View toView(Student s) {
        return new View(s.getId(), s.getRollNumber(), s.getAdmissionNumber(), s.getFirstName(),
                s.getLastName(), s.getDob(), s.getGender(), s.getEmail(), s.getPhone(),
                s.getBloodGroup(), s.getPhotoUrl(), s.getDepartmentId(), s.getCourseId(),
                s.getSemester(), s.getStatus(),
                s.getParent() == null ? null : new ParentReq(
                        s.getParent().getFatherName(), s.getParent().getFatherPhone(),
                        s.getParent().getMotherName(), s.getParent().getMotherPhone(),
                        s.getParent().getGuardianName(), s.getParent().getGuardianPhone()),
                s.getAddress() == null ? null : new AddressReq(
                        s.getAddress().getLine1(), s.getAddress().getLine2(),
                        s.getAddress().getCity(), s.getAddress().getState(),
                        s.getAddress().getCountry(), s.getAddress().getPostalCode()));
    }

    public static Student fromCreate(CreateReq r) {
        Student s = Student.builder()
                .rollNumber(r.rollNumber()).admissionNumber(r.admissionNumber())
                .firstName(r.firstName()).lastName(r.lastName())
                .dob(r.dob()).gender(r.gender()).email(r.email()).phone(r.phone())
                .bloodGroup(r.bloodGroup()).photoUrl(r.photoUrl())
                .departmentId(r.departmentId()).courseId(r.courseId()).semester(r.semester())
                .status("ACTIVE").build();
        if (r.parent() != null) s.setParent(Parent.builder().student(s)
                .fatherName(r.parent().fatherName()).fatherPhone(r.parent().fatherPhone())
                .motherName(r.parent().motherName()).motherPhone(r.parent().motherPhone())
                .guardianName(r.parent().guardianName()).guardianPhone(r.parent().guardianPhone()).build());
        if (r.address() != null) s.setAddress(Address.builder().student(s)
                .line1(r.address().line1()).line2(r.address().line2()).city(r.address().city())
                .state(r.address().state()).country(r.address().country()).postalCode(r.address().postalCode()).build());
        return s;
    }

    public static void applyUpdate(Student s, UpdateReq r) {
        if (r.firstName() != null) s.setFirstName(r.firstName());
        if (r.lastName() != null) s.setLastName(r.lastName());
        if (r.dob() != null) s.setDob(r.dob());
        if (r.gender() != null) s.setGender(r.gender());
        if (r.email() != null) s.setEmail(r.email());
        if (r.phone() != null) s.setPhone(r.phone());
        if (r.bloodGroup() != null) s.setBloodGroup(r.bloodGroup());
        if (r.photoUrl() != null) s.setPhotoUrl(r.photoUrl());
        if (r.departmentId() != null) s.setDepartmentId(r.departmentId());
        if (r.courseId() != null) s.setCourseId(r.courseId());
        if (r.semester() != null) s.setSemester(r.semester());
        if (r.status() != null) s.setStatus(r.status());
        if (r.parent() != null) {
            Parent p = s.getParent() != null ? s.getParent() : Parent.builder().student(s).build();
            p.setFatherName(r.parent().fatherName());   p.setFatherPhone(r.parent().fatherPhone());
            p.setMotherName(r.parent().motherName());   p.setMotherPhone(r.parent().motherPhone());
            p.setGuardianName(r.parent().guardianName()); p.setGuardianPhone(r.parent().guardianPhone());
            s.setParent(p);
        }
        if (r.address() != null) {
            Address a = s.getAddress() != null ? s.getAddress() : Address.builder().student(s).build();
            a.setLine1(r.address().line1()); a.setLine2(r.address().line2());
            a.setCity(r.address().city());   a.setState(r.address().state());
            a.setCountry(r.address().country()); a.setPostalCode(r.address().postalCode());
            s.setAddress(a);
        }
    }
}
