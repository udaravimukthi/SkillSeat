package com.udara.skillseat.repository;

import com.udara.skillseat.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    boolean existsByEmail(String email);
}