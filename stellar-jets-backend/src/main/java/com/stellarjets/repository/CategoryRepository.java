package com.stellarjets.repository;

import com.stellarjets.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    /** Trae categorías que tengan al menos un vuelo activo */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.flights f WHERE f.active = true")
    List<Category> findCategoriesWithActiveFlights();
}
