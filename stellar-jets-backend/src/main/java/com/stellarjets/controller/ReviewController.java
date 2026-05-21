package com.stellarjets.controller;

import com.stellarjets.dto.ReviewDTO;
import com.stellarjets.dto.ReviewRequestDTO;
import com.stellarjets.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{flightId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long flightId) {
        return ResponseEntity.ok(reviewService.getReviews(flightId));
    }

    @PostMapping("/{flightId}")
    public ResponseEntity<ReviewDTO> addOrUpdate(
            @PathVariable Long flightId,
            @Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(reviewService.addOrUpdateReview(flightId, request));
    }
}
