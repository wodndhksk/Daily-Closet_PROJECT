package com.megait.soir.service;

import com.megait.soir.domain.Review;
import com.megait.soir.form.CodyForm;
import com.megait.soir.domain.Cody;
import com.megait.soir.domain.Item;
import com.megait.soir.domain.Member;
import com.megait.soir.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review createNewReview(Item item, String content) {

        Review review = new Review();

        review.setItem(item);
        review.setContent(content);
        reviewRepository.save(review);
        return review;


    }

    public Review createNewReview(Member member, Item item, long parentId, String title, String content) {

        Review review = new Review();

        review.setMember(member);
        review.setItem(item);
        review.setTitle(title);
        review.setContent(content);
        review.setParentId(parentId);
        reviewRepository.save(review);
        return review;

    }

    public Review createNewReview(Member member, Item item, long parentId, String title, String content, String image) {

        Review review = new Review();

        review.setMember(member);
        review.setItem(item);
        review.setTitle(title);
        review.setContent(content);
        review.setParentId(parentId);
        review.setImg(image);
        reviewRepository.save(review);
        return review;

    }

}
