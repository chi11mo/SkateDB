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