package com.stellarjets.repository;

import com.stellarjets.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByFlightId(Long flightId);

    List<Reservation> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE r.flight.id = :flightId " +
           "AND r.checkIn <= :checkOut AND r.checkOut >= :checkIn")
    boolean existsOverlap(@Param("flightId") Long flightId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut);
}
