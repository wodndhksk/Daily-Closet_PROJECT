package com.megait.soir.repository;

import com.megait.soir.domain.ParentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long> {

    public ParentCategory findByName(String name);

}
