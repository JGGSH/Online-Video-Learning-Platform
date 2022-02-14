package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.Dto.ReviewRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.Dto.ReviewResponseDto;
import org.springframework.data.domain.Page;

public interface ReviewService {

    void createReview(ReviewRequestDto reviewRequest);
    void deleteReview(Long reviewId);
    Page<ReviewResponseDto> getReviews(int page, Long lessonId);
}
