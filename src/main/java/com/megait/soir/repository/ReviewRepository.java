package com.megait.soir.repository;

import com.megait.soir.domain.Item;
import com.megait.soir.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    public Review findById(long id);

}
