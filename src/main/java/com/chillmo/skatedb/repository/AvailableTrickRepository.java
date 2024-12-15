package com.chillmo.skatedb.repository;

import com.chillmo.skatedb.entity.AvailableTrick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableTrickRepository extends JpaRepository<AvailableTrick, Long> {
    List<AvailableTrick> findByNameContaining(String name);
    List<AvailableTrick> findByDifficulty(String difficulty);
    List<AvailableTrick> findByNameContainingAndDifficulty(String name, String difficulty);
}