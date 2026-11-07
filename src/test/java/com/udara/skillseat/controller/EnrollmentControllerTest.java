package com.udara.skillseat.controller;

import com.udara.skillseat.entity.Enrollment;
import com.udara.skillseat.entity.EnrollmentStatus;
import com.udara.skillseat.entity.Student;
import com.udara.skillseat.entity.Subject;
import com.udara.skillseat.exception.BusinessException;
import com.udara.skillseat.exception.ResourceNotFoundException;
import com.udara.skillseat.service.EnrollmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    @DisplayName("POST /api/enrollments: valid request → 201 + JSON")
    void enroll_validRequest_returns201() throws Exception {
        Student kasun = Student.builder().id(1L).firstName("Kasun").lastName("Fernando").build();
        Subject spring = Subject.builder().id(2L).code("SPRING201").name("Spring Boot").build();
        Enrollment enrollment = Enrollment.builder().id(5L).student(kasun).subject(spring)
                .status(EnrollmentStatus.ACTIVE).build();

        when(enrollmentService.enroll(1L, 2L)).thenReturn(enrollment);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "studentId": 1, "subjectId": 2 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.studentName").value("Kasun Fernando"))
                .andExpect(jsonPath("$.subjectCode").value("SPRING201"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/enrollments: missing subjectId → 400 + validation error")
    void enroll_missingSubjectId_returns400() throws Exception {
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "studentId": 1 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.subjectId").value("Subject id is required"));
    }

    @Test
    @DisplayName("POST /api/enrollments: seats full → 409")
    void enroll_whenSeatsFull_returns409() throws Exception {
        when(enrollmentService.enroll(3L, 2L))
                .thenThrow(new BusinessException("No seats available in SPRING201"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "studentId": 3, "subjectId": 2 }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("No seats available in SPRING201"));
    }

    @Test
    @DisplayName("PATCH /api/enrollments/{id}/cancel: unknown enrollment → 404")
    void cancel_notFound_returns404() throws Exception {
        when(enrollmentService.cancel(99L))
                .thenThrow(new ResourceNotFoundException("Enrollment", 99L));

        mockMvc.perform(patch("/api/enrollments/99/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Enrollment not found with id: 99"));
    }
}
