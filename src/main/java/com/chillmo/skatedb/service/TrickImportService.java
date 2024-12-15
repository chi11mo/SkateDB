package com.chillmo.skatedb.service;

import com.chillmo.skatedb.entity.AvailableTrick;
import com.chillmo.skatedb.entity.Difficulty;
import com.chillmo.skatedb.repository.AvailableTrickRepository;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
public class TrickImportService {

    private final AvailableTrickRepository trickRepository;

    public TrickImportService(AvailableTrickRepository trickRepository) {
        this.trickRepository = trickRepository;
    }

    public void importTricksFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // Überspringt die Kopfzeile
            while ((line = reader.readNext()) != null) {
                String name = line[0];
                String description = line[1];
                String difficultyStr = line[2];

                try {
                    // Enum-Typ aus String bestimmen
                    Difficulty difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());

                    // Prüfen, ob Trick bereits existiert
                    if (trickRepository.findByNameContaining(name).isEmpty()) {
                        AvailableTrick trick = AvailableTrick.builder()
                                .name(name)
                                .description(description)
                                .difficulty(difficulty)
                                .build();
                        trickRepository.save(trick);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Ungültiger Schwierigkeitsgrad: " + difficultyStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}