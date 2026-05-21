package com.stellarjets.repository;

import com.stellarjets.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFlightIdOrderByCreatedAtDesc(Long flightId);
    Optional<Review> findByUserIdAndFlightId(Long userId, Long flightId);
    boolean existsByUserIdAndFlightId(Long userId, Long flightId);

    @Query("SELECT COALESCE(AVG(r.stars), 0) FROM Review r WHERE r.flight.id = :flightId")
    double avgStarsByFlightId(@Param("flightId") Long flightId);

    long countByFlightId(Long flightId);
}
