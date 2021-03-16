package com.megait.soir.repository;

import com.megait.soir.domain.Member;
import com.megait.soir.domain.Orders;
import com.megait.soir.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Orders findByMemberAndStatus(Member member, Status status);
    // 회원과 status 정보를 가지고 원하는 Orders 객체를 찾는다.
}
