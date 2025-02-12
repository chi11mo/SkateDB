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

    @PostMapping("/import")
    public String importTricks(@RequestParam String filePath) {
        trickImportService.importTricksFromCsv(filePath);
        return "Import abgeschlossen!";
    }
    // Hinzufügen eines neuen Tricks
    @PostMapping
    public ResponseEntity<Trick> addTrick(@RequestBody Trick trick) {
        Trick savedTrick = trickLibraryService.addTrick(trick);
        return ResponseEntity.ok(savedTrick);
    }

    // Abrufen eines einzelnen Tricks nach ID
    @GetMapping("/{id}")
    public ResponseEntity<Trick> getTrickById(@PathVariable Long id) {
        return ResponseEntity.ok(trickLibraryService.getTrickById(id));
    }

    // Liste aller Tricks abrufen
    @GetMapping
    public ResponseEntity<List<Trick>> getAllTricks() {
        return ResponseEntity.ok(trickLibraryService.getAllTricks());
    }

    // Suchen von Tricks nach Namen und Schwierigkeitsgrad
    @GetMapping("/search")
    public ResponseEntity<List<Trick>> searchTricks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(trickLibraryService.searchTricks(name, difficulty));
    }
}