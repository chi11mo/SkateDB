package com.chillmo.skatedb.trick.library.controller;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.library.service.TrickImportService;
import com.chillmo.skatedb.trick.library.service.TrickLibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trick-library")
public class TrickLibraryController {

    private final TrickLibraryService trickLibraryService;
    private final TrickImportService trickImportService;

    public TrickLibraryController(TrickLibraryService trickLibraryService, TrickImportService trickImportService) {
        this.trickLibraryService = trickLibraryService;
        this.trickImportService = trickImportService;
    }

    /**
     * Trigger the import of tricks from a CSV file.
     *
     * @param filePath location of the CSV file
     * @return status message of the import
     */
    @PostMapping("/import")
    public String importTricks(@RequestParam String filePath) {
        trickImportService.importTricksFromCsv(filePath);
        return "Import finished";
    }

    /**
     * Add a new trick to the library.
     *
     * @param trick trick to persist
     * @return the saved trick entity
     */
    @PostMapping
    public ResponseEntity<Trick> addTrick(@RequestBody Trick trick) {
        Trick savedTrick = trickLibraryService.addTrick(trick);
        return ResponseEntity.ok(savedTrick);
    }

    /**
     * Retrieve a single trick by its id.
     *
     * @param id identifier of the trick
     * @return trick with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Trick> getTrickById(@PathVariable Long id) {
        return ResponseEntity.ok(trickLibraryService.getTrickById(id));
    }

    /**
     * Get a list of all tricks in the library.
     */
    @GetMapping
    public ResponseEntity<List<Trick>> getAllTricks() {
        return ResponseEntity.ok(trickLibraryService.getAllTricks());
    }

    /**
     * Search for tricks by name and difficulty.
     *
     * @param name optional name filter
     * @param difficulty optional difficulty filter
     * @return list of matching tricks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Trick>> searchTricks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(trickLibraryService.searchTricks(name, difficulty));
    }
}