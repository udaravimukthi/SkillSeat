package com.udara.skillseat.controller;

import com.udara.skillseat.dto.EnrollmentRequest;
import com.udara.skillseat.dto.EnrollmentResponse;
import com.udara.skillseat.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        var enrollment = enrollmentService.enroll(request.studentId(), request.subjectId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnrollmentResponse.from(enrollment));
    }

    @PatchMapping("/{id}/cancel")
    public EnrollmentResponse cancel(@PathVariable Long id) {
        return EnrollmentResponse.from(enrollmentService.cancel(id));
    }
}