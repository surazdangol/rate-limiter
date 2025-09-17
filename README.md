# Rate Limiter

A Spring Boot-based API rate limiter with Redis integration, supporting user/IP-based limits, subscription tiers, and abuse detection.

---

## Features

- **Rate Limiting by User & IP:**  
  Tracks requests per API Key and IP address combination.
- **Subscription Tiers:**  
  - Free: 100 requests/day  
  - Basic: 1,000 requests/day  
  - Pro: Unlimited requests
- **Abuse Detection:**  
  - Temporary blocking for burst requests  
  - Detects rapid API key switching from the same IP  
  - Identifies and blocks suspicious/automated behavior

---

## Tech Stack

- Java 17
- Spring Boot 3.5.5
- Redis (via `spring-boot-starter-data-redis`)

---

## Getting Started

### Prerequisites

- Java 17
- Maven
- Redis server running locally (default: `localhost:6379`)

### Configuration

Set Redis connection in `src/main/resources/application.yml`:

```
spring:
  redis:
    host: localhost
    port: 6379   
```

---

## Build & Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

## Usage

### Example API Endpoint

For testing purposes, we have a test endpoint `/api/rate-limit-test` protected by the rate limiter.

### cURL Request

```bash
curl -X GET "http://localhost:8080/api/rate-limit-test" -H "x-api-key: <YOUR_API_KEY>"
```

Replace `<YOUR_API_KEY>` with your actual API key.

---

### Expected Responses

#### Success (Within Limit)

```
Rate Limit passed
```

#### Failure (Rate Limit Exceeded)

```json
{
  "timestamp": "2025-09-16T17:45:27.259+00:00",
  "status": 429,
  "error": "Too Many Requests",
  "path": "/rate-limit-test"
}

```

#### Failure (Unauthorized)

```json
{
  "timestamp": "2025-09-16T17:46:37.499+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/rate-limit-test"
}
```

---

## Test Coverage

Unit tests for the daily token bucket rate limiter are provided in  
`src/test/java/com/suraz/ratelimiter/tokenbucket/TokenBucketServiceTest.java`.

**Covered Scenarios:**

- **First Access:**  
  - When an API key and IP combination is accessed for the first time, returns `true`.

- **Multiple Accesses:**  
  - When only one token is left
    - During the first access succeeds, token, returns `true`.
    - During the second access succeeds, token, returns `false`.

- **Token Exhaustion:**  
  - If no tokens are left and the refill period has not elapsed, returns `false`.
  - If no tokens are left but the refill period (e.g. 1 day) has elapsed, returns `true`.

