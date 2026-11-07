package com.udara.skillseat.repository;

import com.udara.skillseat.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);

    boolean existsByCode(String code);

    // N+1 fix: instructor එකත් එකම query එකෙන් (LEFT JOIN) load කරනවා
    @Override
    @EntityGraph(attributePaths = "instructor")
    Page<Subject> findAll(Pageable pageable);

    // Custom JPQL query: seats ඉතුරු subjects විතරයි
    @EntityGraph(attributePaths = "instructor")
    @Query("SELECT s FROM Subject s WHERE s.enrolledCount < s.capacity")
    Page<Subject> findAvailable(Pageable pageable);
}