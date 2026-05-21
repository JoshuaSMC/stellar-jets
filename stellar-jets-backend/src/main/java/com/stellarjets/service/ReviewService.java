package com.stellarjets.service;

import com.stellarjets.dto.ReviewDTO;
import com.stellarjets.dto.ReviewRequestDTO;
import com.stellarjets.entity.Flight;
import com.stellarjets.entity.Review;
import com.stellarjets.entity.User;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.FlightRepository;
import com.stellarjets.repository.ReviewRepository;
import com.stellarjets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviews(Long flightId) {
        return reviewRepository.findByFlightIdOrderByCreatedAtDesc(flightId).stream()
                .map(this::toDTO)
                .toList();
    }

    public ReviewDTO addOrUpdateReview(Long flightId, ReviewRequestDTO request) {
        User user = currentUser();
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));

        Review review = reviewRepository.findByUserIdAndFlightId(user.getId(), flightId)
                .orElse(Review.builder().user(user).flight(flight).build());

        review.setStars(request.getStars());
        review.setComment(request.getComment());
        Review saved = reviewRepository.save(review);

        // Recalculate flight rating and count
        double avg = reviewRepository.avgStarsByFlightId(flightId);
        long count = reviewRepository.countByFlightId(flightId);
        flight.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        flight.setReviewCount((int) count);
        flightRepository.save(flight);

        return toDTO(saved);
    }

    private ReviewDTO toDTO(Review r) {
        return ReviewDTO.builder()
                .id(r.getId())
                .firstName(r.getUser().getFirstName())
                .lastName(r.getUser().getLastName())
                .stars(r.getStars())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
