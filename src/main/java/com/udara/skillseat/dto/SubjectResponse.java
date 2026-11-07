package com.udara.skillseat.dto;

import com.udara.skillseat.entity.Subject;

import java.time.LocalDate;

public record SubjectResponse(
        Long id,
        String code,
        String name,
        String description,
        Integer capacity,
        Integer enrolledCount,
        Integer availableSeats,
        LocalDate startDate,
        Long instructorId,
        String instructorName
) {
    public static SubjectResponse from(Subject s) {
        return new SubjectResponse(
                s.getId(),
                s.getCode(),
                s.getName(),
                s.getDescription(),
                s.getCapacity(),
                s.getEnrolledCount(),
                s.getCapacity() - s.getEnrolledCount(),
                s.getStartDate(),
                s.getInstructor() != null ? s.getInstructor().getId() : null,
                s.getInstructor() != null ? s.getInstructor().getFullName() : null
        );
    }
}