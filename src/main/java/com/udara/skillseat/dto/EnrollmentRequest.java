package com.udara.skillseat.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(

        @NotNull(message = "Student id is required")
        Long studentId,

        @NotNull(message = "Subject id is required")
        Long subjectId
) {
}