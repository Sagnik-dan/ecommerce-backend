# E-Commerce Backend

A production-oriented e-commerce backend built using **Spring Boot**, **Spring Security**, **JWT Authentication**, and **MySQL**. The project follows a layered architecture with DTOs, service classes, repositories, global exception handling, and RESTful APIs.

## Tech Stack

* Java 21 (Project Target)
* Spring Boot 4.1
* Spring Security
* JWT Authentication
* Spring Data JPA (Hibernate)
* MySQL
* Maven
* Lombok
* Jakarta Validation
* Swagger / OpenAPI

---

## Features

### Authentication

* User Registration
* User Login
* JWT Token Generation
* BCrypt Password Encryption
* Stateless Authentication
* Protected Endpoints

### Category Management

* Create Categories
* Duplicate Category Validation (Case-Insensitive)
* DTO-Based Request & Response
* Custom Exception Handling

### Product Management

* Create Product
* Get Product By ID
* Get All Products
* Product-Category Relationship (`@ManyToOne`)
* DTO-Based API Design

### Exception Handling

* Global Exception Handler
* User Already Exists
* Invalid Credentials
* Category Already Exists
* Category Not Found
* Product Not Found

---

## Project Structure

```text
src
└── main
    ├── java
    │   └── com.sagnik.ecommerce_backend
    │       ├── config
    │       ├── controller
    │       ├── dto
    │       ├── entity
    │       ├── exception
    │       ├── repository
    │       ├── security
    │       ├── service
    │       │   └── impl
    │       └── EcommerceBackendApplication
    └── resources
        └── application.properties
```

---

## API Endpoints

### Authentication

| Method | Endpoint             | Description                      |
| ------ | -------------------- | -------------------------------- |
| POST   | `/api/auth/register` | Register a new user              |
| POST   | `/api/auth/login`    | Authenticate user and return JWT |

### Categories

| Method | Endpoint          | Description           |
| ------ | ----------------- | --------------------- |
| POST   | `/api/categories` | Create a new category |

### Products

| Method | Endpoint             | Description       |
| ------ | -------------------- | ----------------- |
| POST   | `/api/products`      | Create a product  |
| GET    | `/api/products`      | Get all products  |
| GET    | `/api/products/{id}` | Get product by ID |

### Test APIs

| Method | Endpoint            | Description                       |
| ------ | ------------------- | --------------------------------- |
| GET    | `/api/test/public`  | Public endpoint                   |
| GET    | `/api/test/private` | Protected endpoint (JWT Required) |

---

## Authentication Flow

1. Register a new user.
2. Login using registered credentials.
3. Receive a JWT token.
4. Include the token in subsequent requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Running the Project

### Clone the Repository

```bash
git clone <repository-url>
```

### Configure Database

Create a MySQL database:

```sql
CREATE DATABASE ecommerce_db;
```

Update `application.properties` with your database credentials and JWT configuration.

### Run the Application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

---

## Future Enhancements

* Product Pagination
* Product Sorting
* Product Search
* Filter Products by Category
* Shopping Cart
* Order Management
* Inventory Management
* Role-Based Authorization (Admin / Customer)
* Docker Support
* Unit & Integration Testing
* CI/CD Pipeline
* API Documentation Improvements

---

## Learning Outcomes

This project demonstrates practical experience with:

* REST API Design
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Entity Relationships
* DTO Pattern
* Layered Architecture
* Exception Handling
* Repository Pattern
* Dependency Injection
* Validation
* Git & GitHub

---

## Author

**Sagnik Dandapat**
