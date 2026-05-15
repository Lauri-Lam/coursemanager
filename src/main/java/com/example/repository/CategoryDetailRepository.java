package com.example.repository;

import com.example.domain.Category;
import com.example.domain.CategoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Long> {

    Optional<CategoryDetail> findByCategory(Category category);
}