package com.megait.soir.repository;

import com.megait.soir.domain.Cody;
import com.megait.soir.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodyRepository extends JpaRepository<Cody, Long> {

    public Member findById(String id);
}
