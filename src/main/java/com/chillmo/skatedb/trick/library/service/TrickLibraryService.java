package com.chillmo.skatedb.trick.library.service;

import com.chillmo.skatedb.trick.domain.Trick;

import java.util.List;

public interface TrickLibraryService {
    Trick addTrick(Trick trick);

    Trick getTrickById(Long id);

    List<Trick> getAllTricks();

    List<Trick> searchTricks(String name, String difficulty);
}