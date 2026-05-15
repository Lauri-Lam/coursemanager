package com.example.service;

import com.example.domain.Category;
import com.example.domain.Course;
import com.example.domain.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Course> searchCourses(
            String name,
            Integer minCredits,
            Boolean active,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            Category selectedCategory,
            String studentLastName
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
        Root<Course> course = criteriaQuery.from(Course.class);

        course.fetch("category", JoinType.LEFT);

        Join<Course, Category> categoryJoin = course.join("category", JoinType.LEFT);
        Join<Course, Student> studentJoin = course.join("students", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(course.get("name")),
                            "%" + name.toLowerCase() + "%"
                    )
            );
        }

        if (minCredits != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(course.get("credits"), minCredits)
            );
        }

        if (active != null) {
            predicates.add(
                    criteriaBuilder.equal(course.get("active"), active)
            );
        }

        if (startDateFrom != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(course.get("startDate"), startDateFrom)
            );
        }

        if (startDateTo != null) {
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(course.get("startDate"), startDateTo)
            );
        }

        if (selectedCategory != null) {
            predicates.add(
                    criteriaBuilder.equal(categoryJoin.get("id"), selectedCategory.getId())
            );
        }

        if (studentLastName != null && !studentLastName.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(studentJoin.get("lastName")),
                            "%" + studentLastName.toLowerCase() + "%"
                    )
            );
        }

        criteriaQuery.select(course).distinct(true);

        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(course.get("startDate")));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}