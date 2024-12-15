package com.chillmo.skatedb.service;

import com.chillmo.skatedb.entity.AvailableTrick;

import java.util.List;

public interface TrickLibraryService {
    AvailableTrick addTrick(AvailableTrick trick);
    AvailableTrick getTrickById(Long id);
    List<AvailableTrick> getAllTricks();
    List<AvailableTrick> searchTricks(String name, String difficulty);
}