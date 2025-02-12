package com.chillmo.skatedb.trick.library.service;

import com.chillmo.skatedb.trick.domain.Difficulty;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.domain.TrickType;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrickImportService {

    private final TrickLibraryRepository trickLibraryRepository;

    public TrickImportService(TrickLibraryRepository trickLibraryRepository) {
        this.trickLibraryRepository = trickLibraryRepository;
    }

    public void importTricksFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // Überspringe die Kopfzeile
            while ((line = reader.readNext()) != null) {
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

                try {
                    // Enum-Typen anhand der Strings bestimmen
                    Difficulty difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
                    TrickType trickType = TrickType.valueOf(trickTypeStr.toUpperCase());

                    // Prüfen, ob der Trick bereits existiert (hier einfache Überprüfung anhand des Namens)
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
                                // Hinweis: Diese Methode kann mehrere Treffer liefern – hier wird der erste Treffer genommen.
                                List<Trick> prereqTricks = trickLibraryRepository.findByNameContaining(prereqName);
                                if (!prereqTricks.isEmpty()) {
                                    prerequisitesList.add(prereqTricks.get(0));
                                } else {
                                    System.err.println("Voraussetzungstrick nicht gefunden für: " + prereqName);
                                }
                            }
                            trick.setPrerequisites(prerequisitesList);
                        }

                        trickLibraryRepository.save(trick);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Ungültiger Schwierigkeitsgrad oder TrickType: "
                            + difficultyStr + " bzw. " + trickTypeStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}