package com.megait.soir.repository;

import com.megait.soir.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Category findByName(String name);
    public void deleteAllByName(String name);

    // parent category + category name과 일치하는 category 조회하기
    public Category findByNameAndParent(String name, Category parent); // naming convention



}
