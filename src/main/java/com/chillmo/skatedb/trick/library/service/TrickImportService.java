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

    /**
     * Import tricks from the given CSV file and persist them in the library.
     * Existing tricks will be ignored.
     *
     * @param filePath path to the CSV file containing trick data
     */
    public void importTricksFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            // Skip the header row
            reader.readNext();

            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                lineNumber++;

                if (line.length < 8) {
                    logger.warn("Skipping line {} - insufficient columns", lineNumber);
                    continue;
                }

                try {
                    // CSV columns:
                    // 0: name
                    // 1: description
                    // 2: difficulty
                    // 3: trick_type
                    // 4: category
                    // 5: imageUrl
                    // 6: videoUrl
                    // 7: prerequisites (trick names separated by semicolon)
                    String name = line[0];
                    String description = line[1];
                    String difficultyStr = line[2];
                    String trickTypeStr = line[3];
                    String category = line[4];
                    String imageUrl = line[5];
                    String videoUrl = line[6];
                    String prerequisitesStr = line[7];

                    // Determine enum values from the provided strings
                    Difficulty difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
                    TrickType trickType = TrickType.valueOf(trickTypeStr.toUpperCase());

                    // Check if the trick already exists
                    if (trickLibraryRepository.findByNameContaining(name).isEmpty()) {
                        // Create the new trick without prerequisites
                        Trick trick = Trick.builder()
                                .name(name)
                                .description(description)
                                .difficulty(difficulty)
                                .trickType(trickType)
                                .category(category)
                                .imageUrl(imageUrl)
                                .videoUrl(videoUrl)
                                .build();

                        // Parse and assign prerequisites if provided
                        if (prerequisitesStr != null && !prerequisitesStr.trim().isEmpty()) {
                            String[] prerequisiteNames = prerequisitesStr.split(";");
                            List<Trick> prerequisitesList = new ArrayList<>();
                            for (String prereqName : prerequisiteNames) {
                                prereqName = prereqName.trim();
                                // Look up a trick whose name contains the given string
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