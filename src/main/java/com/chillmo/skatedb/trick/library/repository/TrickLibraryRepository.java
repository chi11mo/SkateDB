package com.chillmo.skatedb.trick.library.repository;

import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.domain.TrickType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrickLibraryRepository extends JpaRepository<Trick, Long> {
    List<Trick> findByNameContaining(String name);

    List<Trick> findByDifficulty(String difficulty);

    List<Trick> findByNameContainingAndDifficulty(String name, String difficulty);

    List<Trick> findByTrickType(TrickType trickType);

    List<Trick> findByCategoryContaining(String category);
}