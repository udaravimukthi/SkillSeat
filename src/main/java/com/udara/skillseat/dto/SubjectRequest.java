package com.udara.skillseat.dto;

import com.udara.skillseat.entity.Subject;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SubjectRequest(

        @NotBlank(message = "Subject code is required")
        @Size(max = 20, message = "Code must be at most 20 characters")
        String code,

        @NotBlank(message = "Subject name is required")
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 500, message = "Capacity must be at most 500")
        Integer capacity,

        @FutureOrPresent(message = "Start date cannot be in the past")
        LocalDate startDate,

        @NotNull(message = "Instructor id is required")
        Long instructorId
) {
    public Subject toEntity() {
        return Subject.builder()
                .code(code)
                .name(name)
                .description(description)
                .capacity(capacity)
                .startDate(startDate)
                .build();
    }
}