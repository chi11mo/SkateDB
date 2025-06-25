package com.chillmo.skatedb.trick.library.service;

import com.chillmo.skatedb.trick.domain.Trick;

import java.util.List;

public interface TrickLibraryService {

    /**
     * Persist a new trick in the library.
     *
     * @param trick trick to add
     * @return the saved trick entity
     */
    Trick addTrick(Trick trick);

    /**
     * Retrieve a trick by its id.
     *
     * @param id identifier of the trick
     * @return the found trick
     */
    Trick getTrickById(Long id);

    /**
     * Get all tricks stored in the library.
     *
     * @return list of all tricks
     */
    List<Trick> getAllTricks();

    /**
     * Search for tricks optionally filtered by name and difficulty.
     *
     * @param name optional partial name
     * @param difficulty optional difficulty level
     * @return list of matching tricks
     */
    List<Trick> searchTricks(String name, String difficulty);
}