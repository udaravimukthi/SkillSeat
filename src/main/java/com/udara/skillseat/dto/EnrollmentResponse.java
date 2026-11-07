package com.udara.skillseat.dto;

import com.udara.skillseat.entity.Enrollment;
import com.udara.skillseat.entity.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long subjectId,
        String subjectCode,
        String subjectName,
        EnrollmentStatus status,
        LocalDateTime enrolledAt
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getFirstName() + " " + e.getStudent().getLastName(),
                e.getSubject().getId(),
                e.getSubject().getCode(),
                e.getSubject().getName(),
                e.getStatus(),
                e.getEnrolledAt()
        );
    }
}