package com.nhn.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhn.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
