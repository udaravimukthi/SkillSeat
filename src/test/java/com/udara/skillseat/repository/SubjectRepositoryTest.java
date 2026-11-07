package com.udara.skillseat.repository;

import com.udara.skillseat.entity.Student;
import com.udara.skillseat.entity.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        subjectRepository.save(Subject.builder().code("JAVA101").name("Java")
                .capacity(30).enrolledCount(5).build());
        subjectRepository.save(Subject.builder().code("SPRING201").name("Spring")
                .capacity(2).enrolledCount(2).build());
        subjectRepository.save(Subject.builder().code("DB101").name("Database")
                .capacity(25).enrolledCount(0).build());

        studentRepository.save(Student.builder().firstName("Kasun").lastName("Fernando")
                .email("kasun@test.com").build());
        studentRepository.save(Student.builder().firstName("Nimali").lastName("Jayasinghe")
                .email("nimali@test.com").build());
    }

    @Test
    @DisplayName("findAvailable: returns only subjects with free seats")
    void findAvailable_returnsOnlySubjectsWithFreeSeats() {
        Page<Subject> result = subjectRepository.findAvailable(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Subject::getCode)
                .containsExactlyInAnyOrder("JAVA101", "DB101")
                .doesNotContain("SPRING201");
    }

    @Test
    @DisplayName("existsByCode: true for an existing code, false otherwise")
    void existsByCode_works() {
        assertThat(subjectRepository.existsByCode("JAVA101")).isTrue();
        assertThat(subjectRepository.existsByCode("PY999")).isFalse();
    }

    @Test
    @DisplayName("Student name search: case-insensitive, matches first or last name")
    void studentSearch_isCaseInsensitive() {
        Page<Student> byFirst = studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("KAS", "KAS", PageRequest.of(0, 10));
        Page<Student> byLast = studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("singhe", "singhe", PageRequest.of(0, 10));

        assertThat(byFirst.getContent()).extracting(Student::getFirstName).containsExactly("Kasun");
        assertThat(byLast.getContent()).extracting(Student::getFirstName).containsExactly("Nimali");
    }
}
