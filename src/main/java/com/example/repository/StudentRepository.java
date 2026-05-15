package com.example.repository;

import com.example.domain.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = "courses")
    List<Student> findAll();
}