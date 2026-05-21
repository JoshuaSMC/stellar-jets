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

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.flight.id = :flightId " +
           "AND r.checkIn <= :checkOut AND r.checkOut >= :checkIn")
    boolean existsOverlap(@Param("flightId") Long flightId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut);
}
