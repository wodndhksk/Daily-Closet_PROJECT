package com.megait.soir.repository;

import com.megait.soir.domain.Item;
import com.megait.soir.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    public boolean existsByEmail(String email); // email 검색용 method

    public Member findByEmail(String email);
}
