package com.udara.skillseat.dto;

import com.udara.skillseat.entity.Student;
import jakarta.validation.constraints.*;

public record StudentRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits starting with 0")
        String phone
) {
    public Student toEntity() {
        return Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}