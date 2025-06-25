INSERT INTO users (id, username, email, password, enabled)
VALUES (100, 'demo', 'demo@mail.com',
        '{bcrypt}$2b$12$jfJcKBn888IU5GS/uukS3OwF8n3VpbXyt8GFcYWt9ijpF8fEhFcqG', -- Passwort: Demo123!
        TRUE);

-- Rollen als Enum abgebildet – keine Inserts erforderlich