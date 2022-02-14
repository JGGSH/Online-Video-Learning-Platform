package com.georgi.shakev.OnlineVideoLearningPlatform.repository;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> getAllByLessonId(Long lessonId, Pageable pageable);
}
