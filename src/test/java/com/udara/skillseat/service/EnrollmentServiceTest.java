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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Student kasun;
    private Subject spring;

    @BeforeEach
    void setUp() {
        kasun = Student.builder().id(1L).firstName("Kasun").lastName("Fernando")
                .email("kasun@gmail.com").build();

        spring = Subject.builder().id(2L).code("SPRING201").name("Spring Boot")
                .capacity(2).enrolledCount(0).build();
    }

    @Test
    @DisplayName("Enroll: when seats are available, creates enrollment and increases seat count")
    void enroll_success_increasesEnrolledCount() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(kasun));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(spring));
        when(enrollmentRepository.findByStudentIdAndSubjectId(1L, 2L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Enrollment result = enrollmentService.enroll(1L, 2L);

        // Then
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(result.getStudent()).isEqualTo(kasun);
        assertThat(spring.getEnrolledCount()).isEqualTo(1);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Enroll: when seats are full, throws BusinessException and does not save")
    void enroll_whenNoSeats_throwsBusinessException() {
        spring.setEnrolledCount(2); // 2/2 full

        when(studentRepository.findById(1L)).thenReturn(Optional.of(kasun));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(spring));
        when(enrollmentRepository.findByStudentIdAndSubjectId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No seats available in SPRING201");

        verify(enrollmentRepository, never()).save(any());
        assertThat(spring.getEnrolledCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Enroll: when already enrolled in a full subject, returns 'already enrolled' message")
    void enroll_whenAlreadyEnrolledAndFull_returnsAlreadyEnrolledMessage() {
        spring.setEnrolledCount(2);
        Enrollment existing = Enrollment.builder().id(10L).student(kasun).subject(spring)
                .status(EnrollmentStatus.ACTIVE).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(kasun));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(spring));
        when(enrollmentRepository.findByStudentIdAndSubjectId(1L, 2L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Student is already enrolled in SPRING201");
    }

    @Test
    @DisplayName("Enroll: previously cancelled enrollment is reactivated (no new row)")
    void enroll_whenPreviouslyCancelled_reactivatesSameEnrollment() {
        Enrollment cancelled = Enrollment.builder().id(10L).student(kasun).subject(spring)
                .status(EnrollmentStatus.CANCELLED).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(kasun));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(spring));
        when(enrollmentRepository.findByStudentIdAndSubjectId(1L, 2L)).thenReturn(Optional.of(cancelled));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.enroll(1L, 2L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(spring.getEnrolledCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Enroll: when student does not exist, throws ResourceNotFoundException")
    void enroll_whenStudentNotFound_throwsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(99L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found with id: 99");

        verifyNoInteractions(subjectRepository, enrollmentRepository);
    }

    @Test
    @DisplayName("Cancel: cancelling an ACTIVE enrollment releases the seat")
    void cancel_success_releasesSeat() {
        spring.setEnrolledCount(1);
        Enrollment active = Enrollment.builder().id(10L).student(kasun).subject(spring)
                .status(EnrollmentStatus.ACTIVE).build();

        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(active));

        Enrollment result = enrollmentService.cancel(10L);

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        assertThat(spring.getEnrolledCount()).isZero();
    }

    @Test
    @DisplayName("Cancel: an already cancelled enrollment cannot be cancelled again")
    void cancel_whenNotActive_throwsBusinessException() {
        Enrollment cancelled = Enrollment.builder().id(10L).student(kasun).subject(spring)
                .status(EnrollmentStatus.CANCELLED).build();

        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> enrollmentService.cancel(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only active enrollments can be cancelled");
    }
}
