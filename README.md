# Popularity

A Spring Boot application that calculates popularity scores for GitHub repositories based on stars, forks, and recent activity.

## Requirements

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven Wrapper)
- **GitHub API Token** (optional but recommended for higher rate limits)

## How to Run

### Prerequisites

1. Ensure Java 21 is installed:
   ```bash
   java -version
   ```

2. (Optional) Set GitHub API token in `src/main/resources/application.yaml`:
   ```yaml
   http:
     github:
       api:
         token: "your-github-token-here"
   ```
   Note: The application will work without a token, but you'll have stricter rate limits.

### Running the Application

**Using Maven Wrapper (Recommended)**

If we do not have an api token then the rate limit from GitHub api will be 10 requests per minute

```bash
export GITHUB_API_TOKEN=ghp_abc123xyz
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` by default.

## API Endpoints

### Get Popular Repositories

```
GET /api/repositories/popular
```

**Query Parameters:**
- `createdAfter` (required): Filter repositories created after this date (ISO format: YYYY-MM-DD)
- `language` (optional): Filter by programming language (e.g., "Java", "Python")
- `page` (optional): Page number for pagination (default: 1)

**Example:**
```bash
curl "http://localhost:8080/api/repositories/popular?createdAfter=2024-01-01&language=Java&page=1"
```

**Response:**
```json
[
  {
    "name": "owner/repository",
    "createdAt": "2024-01-15T10:30:00Z",
    "popularityScore": 85.5,
    "language": "Java"
  }
]
```

## Additional Features

### API Documentation (Swagger/OpenAPI)

Once the application is running, access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Actuator Endpoints

The application exposes Spring Boot Actuator endpoints for monitoring:
- **Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

### Caching

The application uses Caffeine cache with the following configuration:
- Maximum size: 500 entries
- Expiration: 10 minutes after write
- Cache key: `createdAfter|language|page`

This helps reduce API calls to GitHub and improves response times for repeated queries.

## Running Tests

```bash
./mvnw test
```

## Libraries and Frameworks Used

- **Spring Boot 3.4.11**
- **Spring Cloud OpenFeign** - HTTP client for GitHub API
- **Caffeine** - In-memory caching
- **Springdoc OpenAPI** - API documentation
- **Lombok** - For reducing boilerplate code
- **Spring Boot Actuator** - Monitoring and metrics

