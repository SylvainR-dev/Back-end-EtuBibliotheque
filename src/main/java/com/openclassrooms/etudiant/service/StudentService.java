package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student create(Student student) {
        Assert.notNull(student, "Student must not be null");
        log.info("Creating new student");
        return studentRepository.save(student);
    }

    public List<Student> getAll() {
        log.info("Getting all students");
        return studentRepository.findAll();
    }

    public Optional<Student> getById(Long id) {
        log.info("Getting student by id");
        return studentRepository.findById(id);
    }

    public Student update(Long id, Student student) {
        Assert.notNull(student, "Student must not be null");
        log.info("Updating student");
        student.setId(id);
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        log.info("Deleting student");
        studentRepository.deleteById(id);
    }
}