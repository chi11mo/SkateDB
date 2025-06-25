package com.chillmo.skatedb.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        try {
            // Load .env file from classpath (src/main/resources/.env)
            Dotenv dotenv = Dotenv.configure()
                    .directory("src/main/resources")
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();

            // Set system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                // Only set if not already set (environment variables take precedence)
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });

            System.out.println("✅ .env file loaded successfully");
        } catch (Exception e) {
            System.err.println("⚠️ Could not load .env file: " + e.getMessage());
        }
    }
} 