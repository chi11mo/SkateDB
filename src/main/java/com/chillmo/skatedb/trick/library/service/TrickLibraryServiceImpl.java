package com.chillmo.skatedb.trick.library.service;

import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrickLibraryServiceImpl implements TrickLibraryService {

    private final TrickLibraryRepository trickLibraryRepository;

    public TrickLibraryServiceImpl(TrickLibraryRepository trickLibraryRepository) {
        this.trickLibraryRepository = trickLibraryRepository;
    }

    @Override
    public Trick addTrick(Trick trick) {
        return trickLibraryRepository.save(trick);
    }

    @Override
    public Trick getTrickById(Long id) {
        return trickLibraryRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Trick mit ID " + id + " nicht gefunden."));
    }

    @Override
    public List<Trick> getAllTricks() {
        return trickLibraryRepository.findAll();
    }

    @Override
    public List<Trick> searchTricks(String name, String difficulty) {
        if (name != null && difficulty != null) {
            return trickLibraryRepository.findByNameContainingAndDifficulty(name, difficulty);
        } else if (name != null) {
            return trickLibraryRepository.findByNameContaining(name);
        } else if (difficulty != null) {
            return trickLibraryRepository.findByDifficulty(difficulty);
        } else {
            return trickLibraryRepository.findAll();
        }
    }
}