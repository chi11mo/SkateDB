package com.chillmo.skatedb.controller;

import com.chillmo.skatedb.entity.AvailableTrick;
import com.chillmo.skatedb.service.TrickImportService;
import com.chillmo.skatedb.service.TrickLibraryService;
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
    public ResponseEntity<AvailableTrick> addTrick(@RequestBody AvailableTrick trick) {
        AvailableTrick savedTrick = trickLibraryService.addTrick(trick);
        return ResponseEntity.ok(savedTrick);
    }

    // Abrufen eines einzelnen Tricks nach ID
    @GetMapping("/{id}")
    public ResponseEntity<AvailableTrick> getTrickById(@PathVariable Long id) {
        return ResponseEntity.ok(trickLibraryService.getTrickById(id));
    }

    // Liste aller Tricks abrufen
    @GetMapping
    public ResponseEntity<List<AvailableTrick>> getAllTricks() {
        return ResponseEntity.ok(trickLibraryService.getAllTricks());
    }

    // Suchen von Tricks nach Namen und Schwierigkeitsgrad
    @GetMapping("/search")
    public ResponseEntity<List<AvailableTrick>> searchTricks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(trickLibraryService.searchTricks(name, difficulty));
    }
}