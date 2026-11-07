package com.udara.skillseat.controller;

import com.udara.skillseat.dto.EnrollmentResponse;
import com.udara.skillseat.dto.SubjectRequest;
import com.udara.skillseat.dto.SubjectResponse;
import com.udara.skillseat.service.EnrollmentService;
import com.udara.skillseat.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public List<SubjectResponse> getAll() {
        return subjectService.getAll().stream()
                .map(SubjectResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public SubjectResponse getById(@PathVariable Long id) {
        return SubjectResponse.from(subjectService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        var saved = subjectService.create(request.toEntity(), request.instructorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SubjectResponse.from(saved));
    }

    @GetMapping("/{id}/enrollments")
    public List<EnrollmentResponse> getEnrollments(@PathVariable Long id) {
        return enrollmentService.getBySubject(id).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }
}