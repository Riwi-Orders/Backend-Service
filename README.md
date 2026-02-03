# 🛒 Order Management API

REST API for Order Management System built with **Spring Boot 3**, **PostgreSQL**, and **JWT Authentication**.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [API Endpoints](#-api-endpoints)
- [Authentication](#-authentication)
- [Project Structure](#-project-structure)
- [Environment Variables](#-environment-variables)
- [Docker](#-docker)
- [Business Rules](#-business-rules)
- [Contributing](#-contributing)

---

## ✨ Features

- **JWT Authentication** with secure token generation and validation
- **Role-based Access Control** (USER and ADMIN roles)
- **Product Management** - CRUD operations (Admin only)
- **Order Management** - Create, view, and cancel orders
- **Swagger/OpenAPI** documentation
- **Docker** support for containerized deployment
- **PostgreSQL** database with JPA/Hibernate

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Runtime |
| Spring Boot | 3.5.10 | Framework |
| Spring Security | 6.x | Authentication & Authorization |
| PostgreSQL | 16 | Database |
| JJWT | 0.12.6 | JWT Token handling |
| SpringDoc OpenAPI | 2.8.4 | API Documentation |
| Lombok | Latest | Boilerplate reduction |
| Maven | 3.x | Build tool |

---

## 📦 Prerequisites

- **Java 17** or higher
- **Maven 3.6** or higher
- **PostgreSQL 14** or higher (or Docker)
- **Docker & Docker Compose** (optional, for containerized deployment)

---

## 🚀 Getting Started

### Option 1: Run with Docker (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd backend

# Copy environment file
cp .env.example .env

# Start services (PostgreSQL + Backend)
docker-compose up -d

# View logs
docker-compose logs -f backend
```

The API will be available at: `http://localhost:8080`

### Option 2: Run Locally

1. **Start PostgreSQL** and create database:
```sql
CREATE DATABASE order_management;
```

2. **Configure environment variables** (or edit `application.yaml`):
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -base64 32)
```

3. **Build and run**:
```bash
./mvnw spring-boot:run
```

---

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

---

## 🔗 API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/register` | Register new user | Public |
| POST | `/login` | Login and get JWT | Public |

### Users (`/api/users`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/me` | Get current user profile | Authenticated |
| GET | `/` | List all users | ADMIN |
| GET | `/{id}` | Get user by ID | ADMIN |
| PUT | `/{id}/promote` | Promote to admin | ADMIN |

### Products (`/api/products`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/` | List all products | Public |
| GET | `/active` | List active products | Public |
| GET | `/search?q=` | Search products | Public |
| GET | `/{id}` | Get product by ID | Public |
| POST | `/` | Create product | ADMIN |
| PUT | `/{id}` | Update product | ADMIN |
| PUT | `/{id}/deactivate` | Deactivate product | ADMIN |
| DELETE | `/{id}` | Delete product | ADMIN |

### Orders (`/api/orders`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/` | Create order | USER |
| GET | `/my-orders` | Get my orders | USER |
| GET | `/{id}` | Get order by ID | USER/ADMIN |
| PUT | `/{id}/cancel` | Cancel order | USER |
| GET | `/` | List all orders | ADMIN |
| GET | `/status?status=` | Filter by status | ADMIN |
| PUT | `/{id}/status` | Update status | ADMIN |

---

## 🔐 Authentication

The API uses **JWT (JSON Web Token)** for authentication.

### Login Flow

1. **Register** a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

2. **Login** to get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

3. **Use token** in subsequent requests:
```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Token Payload

```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

---

## 📁 Project Structure

```
src/main/java/com/riwi/order_management/
├── config/
│   ├── SecurityConfig.java       # Spring Security configuration
│   └── OpenApiConfig.java        # Swagger/OpenAPI configuration
├── controller/
│   ├── AuthController.java       # Authentication endpoints
│   ├── UserController.java       # User management
│   ├── ProductController.java    # Product CRUD
│   └── OrderController.java      # Order management
├── dto/
│   ├── request/                  # Request DTOs
│   └── response/                 # Response DTOs
├── entity/
│   ├── User.java                 # User entity
│   ├── Product.java              # Product entity
│   ├── Order.java                # Order entity
│   ├── OrderItem.java            # Order item entity
│   ├── UserRole.java             # USER/ADMIN enum
│   └── OrderStatus.java          # Order status enum
├── exception/
│   └── GlobalExceptionHandler.java
├── mapper/                       # Entity-DTO mappers
├── repository/                   # JPA repositories
├── security/
│   ├── JwtTokenProvider.java     # JWT generation/validation
│   ├── JwtAuthenticationFilter.java
│   ├── UserPrincipal.java
│   └── CustomUserDetailsService.java
└── service/                      # Business logic layer
```

---

## ⚙️ Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | `postgres` |
| `JWT_SECRET` | Base64 encoded secret key | (provided) |
| `JWT_EXPIRATION` | Token expiration in ms | `86400000` (24h) |

### Generate a Secure JWT Secret

```bash
openssl rand -base64 32
```

---

## 🐳 Docker

### Build and Run

```bash
# Build image only
docker build -t order-management-api .

# Run with docker-compose (includes PostgreSQL)
docker-compose up -d

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Docker Compose Services

| Service | Port | Description |
|---------|------|-------------|
| backend | 8080 | Spring Boot API |
| postgres | 5432 | PostgreSQL Database |

---

## 📋 Business Rules

### Access Control
| Rule | Description |
|------|-------------|
| 1 | USER cannot access ADMIN routes |
| 2 | ADMIN cannot create orders |
| 3 | USER can only view their own orders |
| 4 | Only ADMIN can change order status |
| 5 | USER can cancel only PENDING orders |

### Validations
| Rule | Description |
|------|-------------|
| 6 | Order total calculated in backend |
| 7 | Price copied from product at order time |
| 8 | Only active products can be ordered |
| 9 | Order must have at least 1 item |
| 10 | Email must be unique |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'feat: add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

**Built with ❤️ by Riwi Orders Team**