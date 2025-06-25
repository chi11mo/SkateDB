# SkateDB

**SkateDB** is a Spring Boot-based web application designed to help skateboard enthusiasts track the tricks they’ve learned, receive recommendations for new, more challenging tricks, and manage their user profiles securely using JWT-based authentication.

## Features

- **User Authentication & Authorization**:
    - Secure registration and login endpoints.
    - Passwords stored as hashed values using `BCryptPasswordEncoder`.
    - JWT-based stateless authentication for protected endpoints.

- **Trick Management**:
    - Add new tricks you’ve learned.
    - Track trick difficulty levels using enums (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`).
    - Retrieve lists of tricks based on user progress.

- **Trick Recommendations**:
    - Recommend harder tricks to learn next, based on your current skill level and previously learned tricks.

## Technologies Used

- **Backend**:
    - Java 17+ (or your current JDK)
    - Spring Boot 3.x
    - Spring Data JPA & Hibernate for database interaction
    - Spring Security & JWT for authentication and authorization
    - H2 In-Memory Database (for development/testing)

- **Build & Dependency Management**:
    - Maven (pom.xml)

- **API Testing**:
    - Postman or cURL for sending HTTP requests.

## Getting Started

### Prerequisites

- Java 17+ installed
- Maven 3.8+ installed
- (Optional) Docker if you plan to run using a container in the future

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/SkateDB.git
   cd SkateDB
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean package
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

   The application will start on `http://localhost:8080` by default.

4. **Configuration**:
   Create a `.env` file in `src/main/resources` or set these variables in your environment:
   ```bash
   jwt.secret=change_me
   jwt.expirationMs=3600000
   spring.mail.username=you@example.com
   ```

## Template Users

When the application starts, four example users are created automatically. You can use these accounts to log in immediately.

| Username | Email | Password | Mastered Tricks |
|-----------|-----------------|-------------|----------------------------------|
| admin | admin@example.com | adminpass | |
| tonyhawk | tony@example.com | password123 | Kickflip, Laser Flip, Ollie |
| janedoe | jane@example.com | password123 | Heelflip, Shove-it, Ollie |
| bobsmith | bob@example.com | password123 | Ollie |

