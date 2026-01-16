# Ledger Service

A RESTful microservice for managing customer accounts and financial transactions, built with Spring Boot.

## Requirements

- Java 25+ (or Docker)
- Maven 3.9+ (or use the included Maven Wrapper)

## Quick Start

### Running Locally

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8090`

### Running with Docker

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build and run manually
docker build -t ledger-service .
docker run -p 8090:8090 ledger-service
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage report (generated at target/site/jacoco/index.html)
./mvnw test jacoco:report
```

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8090/api-docs

## API Endpoints

### Accounts

#### Create Account
```http
POST /accounts
Content-Type: application/json

{
  "document_number": "12345678900"
}
```

**Response (201 Created):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### Get Account
```http
GET /accounts/{accountId}
```

**Response (200 OK):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

### Transactions

#### Create Transaction
```http
POST /transactions
Content-Type: application/json

{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

**Response (201 Created):**
```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

### Operation Types

| ID | Description           | Amount Sign |
|----|-----------------------|-------------|
| 1  | PURCHASE              | Negative    |
| 2  | INSTALLMENT PURCHASE  | Negative    |
| 3  | WITHDRAWAL            | Negative    |
| 4  | PAYMENT               | Positive    |

> **Note:** The API accepts positive amounts in the request body. The system automatically converts the amount to negative for debit operations (types 1, 2, 3).

## Additional Endpoints

- **Health Check**: `GET /actuator/health`
- **H2 Console**: http://localhost:8090/h2-console (JDBC URL: `jdbc:h2:mem:ledgerdb`)

## Project Structure

```
src/main/java/io/pismo/ledger/
├── controller/          # REST Controllers
├── domain/entity/       # JPA Entities
├── repository/          # Spring Data JPA Repositories
├── service/             # Business Logic
├── dto/
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
└── exception/           # Custom Exceptions and Handler
```

## Tech Stack

- **Framework**: Spring Boot 3.5.9
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Bean Validation
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build**: Maven

## Author

Filipe Henrique
