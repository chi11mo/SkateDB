package com.chillmo.skatedb;
import com.chillmo.skatedb.service.TrickImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TrickDataLoader implements CommandLineRunner {

    private final TrickImportService trickImportService;

    public TrickDataLoader(TrickImportService trickImportService) {
        this.trickImportService = trickImportService;
    }

    @Override
    public void run(String... args) throws Exception {
        String filePath = "src/main/resources/tricks.csv"; // Pfad zur CSV-Datei
        trickImportService.importTricksFromCsv(filePath);
    }
}