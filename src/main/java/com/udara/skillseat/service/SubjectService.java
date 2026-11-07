package com.udara.skillseat.service;

import com.udara.skillseat.entity.Instructor;
import com.udara.skillseat.entity.Subject;
import com.udara.skillseat.exception.BusinessException;
import com.udara.skillseat.exception.ResourceNotFoundException;
import com.udara.skillseat.repository.InstructorRepository;
import com.udara.skillseat.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final InstructorRepository instructorRepository;

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    @Transactional
    public Subject create(Subject subject, Long instructorId) {
        if (subjectRepository.existsByCode(subject.getCode())) {
            throw new BusinessException("Subject code already exists: " + subject.getCode());
        }
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        subject.setInstructor(instructor);
        return subjectRepository.save(subject);
    }
}