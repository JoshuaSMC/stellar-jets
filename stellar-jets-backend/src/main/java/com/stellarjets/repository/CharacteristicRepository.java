package com.stellarjets.repository;

import com.stellarjets.entity.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacteristicRepository extends JpaRepository<Characteristic, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Characteristic> findAllByOrderByNameAsc();
}
