package com.chillmo.skatedb.service;

import com.chillmo.skatedb.entity.AvailableTrick;
import com.chillmo.skatedb.repository.AvailableTrickRepository;
import com.chillmo.skatedb.service.TrickLibraryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrickLibraryServiceImpl implements TrickLibraryService {

    private final AvailableTrickRepository trickRepository;

    public TrickLibraryServiceImpl(AvailableTrickRepository trickRepository) {
        this.trickRepository = trickRepository;
    }

    @Override
    public AvailableTrick addTrick(AvailableTrick trick) {
        return trickRepository.save(trick);
    }

    @Override
    public AvailableTrick getTrickById(Long id) {
        return trickRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Trick mit ID " + id + " nicht gefunden."));
    }

    @Override
    public List<AvailableTrick> getAllTricks() {
        return trickRepository.findAll();
    }

    @Override
    public List<AvailableTrick> searchTricks(String name, String difficulty) {
        if (name != null && difficulty != null) {
            return trickRepository.findByNameContainingAndDifficulty(name, difficulty);
        } else if (name != null) {
            return trickRepository.findByNameContaining(name);
        } else if (difficulty != null) {
            return trickRepository.findByDifficulty(difficulty);
        } else {
            return trickRepository.findAll();
        }
    }
}