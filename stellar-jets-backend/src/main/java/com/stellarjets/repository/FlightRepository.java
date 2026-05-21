package com.stellarjets.repository;

import com.stellarjets.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByCategoryId(Long categoryId);

    Page<Flight> findByActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCaseAndIdNot(String flightNumber, Long id);

    Page<Flight> findByActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.active = true AND f.category.id IN :categoryIds")
    Page<Flight> findByActiveTrueAndCategoryIdIn(@Param("categoryIds") java.util.List<Long> categoryIds, Pageable pageable);

    @Query("""
            SELECT f FROM Flight f
            WHERE f.active = true
              AND (LOWER(f.name)                  LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.origin.city)            LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.origin.iataCode)        LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.destination.city)       LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.destination.iataCode)   LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Flight> searchActive(@Param("q") String query, Pageable pageable);

    @Query(value = "SELECT * FROM flights WHERE active = true ORDER BY RAND()",
           countQuery = "SELECT COUNT(*) FROM flights WHERE active = true",
           nativeQuery = true)
    Page<Flight> findByActiveTrueRandom(Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.active = true ORDER BY f.rating DESC")
    Page<Flight> findTopRated(Pageable pageable);

    /** Vuelos activos sin reservas que se superpongan con el rango dado */
    @Query("""
            SELECT f FROM Flight f WHERE f.active = true
            AND f.id NOT IN (
                SELECT r.flight.id FROM Reservation r
                WHERE r.checkIn < :checkOut AND r.checkOut > :checkIn
            )
            """)
    Page<Flight> findAvailableInDateRange(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            Pageable pageable);

    /** Búsqueda por texto Y disponibilidad en rango de fechas */
    @Query("""
            SELECT f FROM Flight f WHERE f.active = true
            AND (LOWER(f.name)                  LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(f.origin.city)            LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(f.origin.iataCode)        LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(f.destination.city)       LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(f.destination.iataCode)   LIKE LOWER(CONCAT('%', :q, '%')))
            AND f.id NOT IN (
                SELECT r.flight.id FROM Reservation r
                WHERE r.checkIn < :checkOut AND r.checkOut > :checkIn
            )
            """)
    Page<Flight> searchActiveInDateRange(
            @Param("q") String query,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            Pageable pageable);
}
