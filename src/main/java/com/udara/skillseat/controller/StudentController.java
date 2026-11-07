package com.udara.skillseat.controller;

import com.udara.skillseat.dto.EnrollmentResponse;
import com.udara.skillseat.dto.StudentRequest;
import com.udara.skillseat.dto.StudentResponse;
import com.udara.skillseat.service.EnrollmentService;
import com.udara.skillseat.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public List<StudentResponse> getAll() {
        return studentService.getAll().stream()
                .map(StudentResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable Long id) {
        return StudentResponse.from(studentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        var saved = studentService.create(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(StudentResponse.from(saved));
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return StudentResponse.from(studentService.update(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/enrollments")
    public List<EnrollmentResponse> getEnrollments(@PathVariable Long id) {
        return enrollmentService.getByStudent(id).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }
}