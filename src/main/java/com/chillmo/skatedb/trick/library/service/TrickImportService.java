package com.chillmo.skatedb.trick.library.service;

import com.chillmo.skatedb.trick.domain.Difficulty;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.domain.TrickType;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrickImportService {

    private static final Logger logger = LoggerFactory.getLogger(TrickImportService.class);
    private final TrickLibraryRepository trickLibraryRepository;

    public TrickImportService(TrickLibraryRepository trickLibraryRepository) {
        this.trickLibraryRepository = trickLibraryRepository;
    }

    public void importTricksFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // Überspringe die Kopfzeile

            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                lineNumber++;

                if (line.length < 8) {
                    logger.warn("Skipping line {} - insufficient columns", lineNumber);
                    continue;
                }

                try {
                    // CSV-Spalten:
                    // 0: name
                    // 1: description
                    // 2: difficulty
                    // 3: trick_type
                    // 4: category
                    // 5: imageUrl
                    // 6: videoUrl
                    // 7: prerequisites (Trick-Namen, durch Semikolon getrennt)
                    String name = line[0];
                    String description = line[1];
                    String difficultyStr = line[2];
                    String trickTypeStr = line[3];
                    String category = line[4];
                    String imageUrl = line[5];
                    String videoUrl = line[6];
                    String prerequisitesStr = line[7];

                    // Enum-Typen anhand der Strings bestimmen
                    Difficulty difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
                    TrickType trickType = TrickType.valueOf(trickTypeStr.toUpperCase());

                    // Prüfen, ob der Trick bereits existiert
                    if (trickLibraryRepository.findByNameContaining(name).isEmpty()) {
                        // Neuen Trick ohne Voraussetzungen erstellen
                        Trick trick = Trick.builder()
                                .name(name)
                                .description(description)
                                .difficulty(difficulty)
                                .trickType(trickType)
                                .category(category)
                                .imageUrl(imageUrl)
                                .videoUrl(videoUrl)
                                .build();

                        // Falls prerequisites angegeben wurden, diese parsen und zuordnen
                        if (prerequisitesStr != null && !prerequisitesStr.trim().isEmpty()) {
                            String[] prerequisiteNames = prerequisitesStr.split(";");
                            List<Trick> prerequisitesList = new ArrayList<>();
                            for (String prereqName : prerequisiteNames) {
                                prereqName = prereqName.trim();
                                // Sucht nach einem Trick, dessen Name den angegebenen String enthält.
                                List<Trick> prereqTricks = trickLibraryRepository.findByNameContaining(prereqName);
                                if (!prereqTricks.isEmpty()) {
                                    prerequisitesList.add(prereqTricks.get(0));
                                } else {
                                    logger.warn("Line {}: Prerequisite trick not found: {}", lineNumber, prereqName);
                                }
                            }
                            trick.setPrerequisites(prerequisitesList);
                        }

                        trickLibraryRepository.save(trick);
                        logger.info("Imported trick: {}", name);
                    } else {
                        logger.debug("Trick already exists, skipping: {}", name);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Line {}: Invalid difficulty or trick type: {} / {}",
                            lineNumber, line[2], line[3]);
                } catch (Exception e) {
                    logger.error("Line {}: Error processing line: {}", lineNumber, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error during CSV import from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("CSV import failed", e);
        }
    }
}