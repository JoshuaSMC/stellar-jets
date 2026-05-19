package com.stellarjets.repository;

import com.stellarjets.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findByActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCaseAndIdNot(String flightNumber, Long id);

    Page<Flight> findByActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    /** Búsqueda en nombre, ciudad/IATA de origen y destino */
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

    /** Vuelos activos en orden aleatorio (ORDER BY RAND() — soportado en H2) */
    @Query(value = "SELECT * FROM flights WHERE active = true ORDER BY RAND()",
           countQuery = "SELECT COUNT(*) FROM flights WHERE active = true",
           nativeQuery = true)
    Page<Flight> findByActiveTrueRandom(Pageable pageable);

    /** Top vuelos por rating para sección recomendados */
    @Query("SELECT f FROM Flight f WHERE f.active = true ORDER BY f.rating DESC")
    Page<Flight> findTopRated(Pageable pageable);
}
