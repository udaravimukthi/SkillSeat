package com.udara.skillseat.service;

import com.udara.skillseat.entity.Enrollment;
import com.udara.skillseat.entity.EnrollmentStatus;
import com.udara.skillseat.entity.Student;
import com.udara.skillseat.entity.Subject;
import com.udara.skillseat.exception.BusinessException;
import com.udara.skillseat.exception.ResourceNotFoundException;
import com.udara.skillseat.repository.EnrollmentRepository;
import com.udara.skillseat.repository.StudentRepository;
import com.udara.skillseat.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public Enrollment enroll(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        if (!subject.hasAvailableSeats()) {
            throw new BusinessException("No seats available in " + subject.getCode());
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndSubjectId(studentId, subjectId)
                .orElse(null);

        if (enrollment != null && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
            throw new BusinessException("Student is already enrolled in " + subject.getCode());
        }

        if (enrollment != null) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
        } else {
            enrollment = Enrollment.builder()
                    .student(student)
                    .subject(subject)
                    .build();
        }

        subject.setEnrolledCount(subject.getEnrolledCount() + 1);

        log.info("Student {} enrolled in {}", studentId, subject.getCode());
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment cancel(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BusinessException("Only active enrollments can be cancelled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        Subject subject = enrollment.getSubject();
        subject.setEnrolledCount(subject.getEnrolledCount() - 1);

        log.info("Enrollment {} cancelled", enrollmentId);
        return enrollment;
    }

    public List<Enrollment> getByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getBySubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject", subjectId);
        }
        return enrollmentRepository.findBySubjectId(subjectId);
    }
}