package com.udara.skillseat.config;

import com.udara.skillseat.entity.Instructor;
import com.udara.skillseat.entity.Student;
import com.udara.skillseat.entity.Subject;
import com.udara.skillseat.repository.InstructorRepository;
import com.udara.skillseat.repository.StudentRepository;
import com.udara.skillseat.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        if (instructorRepository.count() > 0) {
            return;
        }

        Instructor perera = instructorRepository.save(Instructor.builder()
                .fullName("Nimal Perera")
                .email("nimal.perera@skillseat.lk")
                .expertise("Java, Spring Boot")
                .build());

        Instructor silva = instructorRepository.save(Instructor.builder()
                .fullName("Kumari Silva")
                .email("kumari.silva@skillseat.lk")
                .expertise("Databases, SQL")
                .build());

        subjectRepository.saveAll(List.of(
                Subject.builder()
                        .code("JAVA101")
                        .name("Java Programming")
                        .description("Core Java from basics to OOP")
                        .capacity(30)
                        .startDate(LocalDate.now().plusDays(7))
                        .instructor(perera)
                        .build(),
                Subject.builder()
                        .code("SPRING201")
                        .name("Spring Boot Fundamentals")
                        .description("Build REST APIs with Spring Boot")
                        .capacity(2)
                        .startDate(LocalDate.now().plusDays(14))
                        .instructor(perera)
                        .build(),
                Subject.builder()
                        .code("DB101")
                        .name("Database Design")
                        .description("ER diagrams, normalization and SQL")
                        .capacity(25)
                        .startDate(LocalDate.now().plusDays(10))
                        .instructor(silva)
                        .build()
        ));

        studentRepository.saveAll(List.of(
                Student.builder().firstName("Kasun").lastName("Fernando")
                        .email("kasun@gmail.com").phone("0771234567").build(),
                Student.builder().firstName("Nimali").lastName("Jayasinghe")
                        .email("nimali@gmail.com").phone("0719876543").build(),
                Student.builder().firstName("Ruwan").lastName("Bandara")
                        .email("ruwan@gmail.com").phone("0765554443").build()
        ));

        log.info("✅ Sample data loaded: 2 instructors, 3 subjects, 3 students");
    }
}