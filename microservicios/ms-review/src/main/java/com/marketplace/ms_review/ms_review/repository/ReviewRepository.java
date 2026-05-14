package com.marketplace.ms_review.ms_review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.ms_review.ms_review.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
}
