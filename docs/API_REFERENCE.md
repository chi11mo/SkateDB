# API Reference

This document lists the available REST endpoints provided by the SkateDB application and summarises the main domain entities.

## Endpoints

### Authentication & Registration

| Method | Path | Description |
| ------ | ---- | ----------- |
| `POST` | `/api/auth/login` | Authenticate a user and receive a JWT token. |
| `POST` | `/api/register` | Register a new user. |
| `GET` | `/api/token/confirm` | Confirm a registration token via `token` query parameter. |
| `POST` | `/api/token/renew` | Renew an expired confirmation token via `token` query parameter. |

### User Management

| Method | Path | Description | Authorization |
| ------ | ---- | ----------- | -------------- |
| `GET` | `/api/users` | List all registered users. | `ROLE_ADMIN` |
| `GET` | `/api/users/me` | Retrieve the authenticated user's profile. | Authenticated user |
| `PUT` | `/api/users/me` | Update the authenticated user's profile details. | Authenticated user |
| `PUT` | `/api/users/me/password` | Change the authenticated user's password. | Authenticated user |
| `DELETE` | `/api/users/me` | Permanently delete the authenticated user's account. | Authenticated user |
| `PUT` | `/api/users/{id}/enable` | Enable a user account. | `ROLE_ADMIN` |
| `PUT` | `/api/users/{id}/roles` | Replace the set of roles assigned to a user. | `ROLE_ADMIN` |

### Email

| Method | Path | Description |
| ------ | ---- | ----------- |
| `POST` | `/api/email/test` | Send a simple test email. |
| `POST` | `/api/email/confirm` | Send an email containing a confirmation link for a token. |

### Trick Library

| Method | Path | Description |
| ------ | ---- | ----------- |
| `POST` | `/api/trick-library/import` | Import tricks from a CSV file (`filePath` query param). |
| `POST` | `/api/trick-library` | Add a new trick to the library. |
| `GET` | `/api/trick-library/{id}` | Retrieve a trick by its id. |
| `GET` | `/api/trick-library` | Retrieve all tricks. |
| `GET` | `/api/trick-library/search` | Search tricks by optional `name` and `difficulty`. |

### User Tricks

| Method | Path | Description |
| ------ | ---- | ----------- |
| `POST` | `/api/user-tricks/start` | Connect a user with a trick to start learning it. |

### Recommendations

| Method | Path | Description |
| ------ | ---- | ----------- |
| `GET` | `/api/tricks/recommendations/{userId}` | Recommend next tricks for the given user. |

## Domain Entities

### Trick
Defined in `Trick.java` with the following key fields:

- `id` – primary key
- `name`
- `description`
- `difficulty` (`Difficulty` enum)
- `trickType` (`TrickType` enum)
- `category`
- `imageUrl`
- `videoUrl`
- `prerequisites` – list of other `Trick` records

### User
Defined in `User.java` with fields such as:

- `id` – primary key
- `username`
- `email`
- `password` (hashed)
- `createdAt`, `lastLogin`
- `profilePictureUrl`, `bio`, `location`
- `experienceLevel` (`ExperienceLevel` enum)
- `roles` (set of `Role` enum values)
- `stand` (`Stand` enum)
- `enabled` flag

### UserTrick
Represents which trick a user started or mastered:

- `id`
- reference to `User`
- reference to `Trick`
- `dateLearned`
- `status` (`TrickStatus` enum)

### ConfirmationToken
Stores registration confirmation tokens:

- `id`
- `token`
- `createdAt`
- `expiresAt`
- reference to `User`

### Enumerations

- `Difficulty` – `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`
- `TrickType` – `VERT`, `STREET`
- `TrickStatus` – `NOT_STARTED`, `IN_PROGRESS`, `MASTERED`
- `ExperienceLevel` – `NOVICE`, `INTERMEDIATE`, `SKILLED`, `PRO`
- `Role` – `ROLE_USER`, `ROLE_ADMIN`, `ROLE_MODERATOR`
- `Stand` – `Goofy`, `Regular`

This overview should help when interacting with the SkateDB API and understanding the database structure.

