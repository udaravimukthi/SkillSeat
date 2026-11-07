package com.udara.skillseat.repository;

import com.udara.skillseat.entity.Enrollment;
import com.udara.skillseat.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndSubjectIdAndStatus(Long studentId, Long subjectId, EnrollmentStatus status);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findBySubjectId(Long subjectId);

    Optional<Enrollment> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}