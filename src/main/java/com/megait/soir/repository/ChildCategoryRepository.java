package com.megait.soir.repository;

import com.megait.soir.domain.ChildCategory;
import com.megait.soir.domain.ParentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildCategoryRepository extends JpaRepository<ChildCategory, Long> {

    public ChildCategory findByname(String name);

}
