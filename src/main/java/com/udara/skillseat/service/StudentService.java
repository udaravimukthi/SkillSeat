package com.udara.skillseat.service;

import com.udara.skillseat.entity.Student;
import com.udara.skillseat.exception.BusinessException;
import com.udara.skillseat.exception.ResourceNotFoundException;
import com.udara.skillseat.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public Student getById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    @Transactional
    public Student create(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new BusinessException("Email already registered: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student update(Long id, Student updated) {
        Student existing = getById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhone(updated.getPhone());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        Student student = getById(id);
        if (!student.getEnrollments().isEmpty()) {
            throw new BusinessException("Cannot delete a student who has enrollments");
        }
        studentRepository.delete(student);
    }
}