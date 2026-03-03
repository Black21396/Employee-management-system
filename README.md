# Employee Management System

A full-stack web application for managing employee records with role-based access control using (JWT). Built with Spring Boot backend and Next.js frontend, featuring JWT authentication and RESTful API design.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Project](#running-the-project)
  - [Development Mode (Hot Reload)](#development-mode-hot-reload)
  - [Production Mode](#production-mode)
- [API Endpoints](#api-endpoints)
- [Authentication & Authorization](#authentication--authorization)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Contributing](#contributing)

---

## 🎯 Overview

This is a modern employee management system that demonstrates a complete full-stack application with:

- **Backend**: RESTful API built with Spring Boot, Spring Security, and JWT authentication
- **Frontend**: Server-side rendered Next.js application with TypeScript and Bootstrap 5
- **Database**: MySQL
- **Containerization**: Docker & Docker Compose for easy deployment

The application supports two user roles:

- **USER**: Read-only access to employee list
- **ADMIN**: Full CRUD operations on employee records

---

## ✨ Features

### Authentication & Authorization

- User registration with email validation
- Secure login with JWT token-based authentication
- Role-based access control (USER/ADMIN)
- Protected routes and API endpoints
- Automatic token refresh and session management

### Employee Management

- View all employees (USER & ADMIN)
- Create new employee records (ADMIN only)
- Edit existing employee details (ADMIN only)
- Delete employee records (ADMIN only)
- Input validation on both frontend and backend

### User Experience

- Responsive design with Bootstrap 5
- Loading states and error handling
- Confirmation dialogs for destructive actions
- Client-side and server-side validation
- Clean and intuitive UI

---

## 🛠 Technology Stack

### Backend

- **Java 17+**
- **Spring Boot 4.x**
- **Spring Security** with JWT
- **Spring Data JPA**
- **MySQL**
- **Maven** for dependency management

### Frontend

- **Next.js 16** (App Router)
- **TypeScript**
- **React 19**
- **Bootstrap 5**
- **Context API** for state management

### DevOps

- **Docker** & **Docker Compose**
- **Hot reload** in development mode
- **Multi-stage builds** for production

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Client (Browser)                     │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/HTTPS
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    Next.js Frontend                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Pages: Login, Register, Employee List, CRUD Forms     │ │
│  │  Services: authService, employeeService                │ │
│  │  Context: AuthContext (JWT + Role management)          │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────────┘
                      │ REST API (JSON)
                      │ Authorization: Bearer <JWT>
┌─────────────────────▼───────────────────────────────────────┐
│                  Spring Boot Backend                         │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Controllers: AuthController, EmployeeController       │ │
│  │  Security: JWT Filter, UserDetailsService              │ │
│  │  Services: AuthService, EmployeeService                │ │
│  │  Repositories: UserRepository, EmployeeRepository      │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────────┘
                      │ JPA/Hibernate
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    PostgreSQL Database                       │
│  Tables: users, employees                                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Docker** (version 20.x or higher)
- **Docker Compose** (version 2.x or higher)
- **Git**

That's it! Docker will handle all other dependencies.

---

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd <project-directory>
```

### 2. Configure Environment Variables

The project includes an `.env.template` file with all required environment variables. You need to create your own `.env` files:

```bash
# Copy the template for production
cp .env.template .env

# Copy the template for development (if different)
cp .env.template .env.dev
```

### 3. Fill in the Environment Variables

Open the `.env` (and `.env.dev`) files and fill in the required values:

```env
# Database Configuration
POSTGRES_DB=employee_db
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_secure_password

# Spring Boot Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/employee_db
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here_at_least_256_bits

# Next.js Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

> ⚠️ **Important**: Never commit `.env` or `.env.dev` files to version control. They contain sensitive information.

---

## 🎮 Running the Project

This project can be run in two modes:

### Development Mode (Hot Reload)

Use this mode during active development. Changes to your code will automatically reload the application.

```bash
docker compose --env-file .env.dev -f docker-compose.dev.yml up
```

**What happens in dev mode:**

- Backend runs with Spring Boot DevTools (auto-restart on changes)
- Frontend runs with Next.js development server (hot module replacement)
- Database data persists in Docker volumes
- Ports exposed:
  - Frontend: `http://localhost:3000`
  - Backend: `http://localhost:8080`
  - Database: `localhost:5432`

**To stop:**

```bash
docker compose --env-file .env.dev -f docker-compose.dev.yml down
```

---

### Production Mode

Use this mode for production deployment or testing production builds.

```bash
docker compose --env-file .env -f docker-compose.yml up
```

**What happens in production mode:**

- Backend built as optimized JAR file
- Frontend built as optimized static bundle
- Multi-stage Docker builds for smaller image sizes
- Security-hardened configurations
- Ports exposed:
  - Frontend: `http://localhost:3000`
  - Backend: `http://localhost:8080`

**To stop:**

```bash
docker compose --env-file .env -f docker-compose.yml down
```

**To run in detached mode (background):**

```bash
docker compose --env-file .env -f docker-compose.yml up -d
```

---

## 🔌 API Endpoints

### Authentication Endpoints

| Method | Endpoint             | Description             | Access |
| ------ | -------------------- | ----------------------- | ------ |
| POST   | `/api/auth/register` | Register new user       | Public |
| POST   | `/api/auth/login`    | Login and get JWT token | Public |

**Register Request Body:**

```json
{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "securePassword123",
  "role": "USER"
}
```

**Login Request Body:**

```json
{
  "username": "johndoe",
  "password": "securePassword123"
}
```

**Response (both endpoints):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER"
}
```

---

### Employee Endpoints

All employee endpoints require `Authorization: Bearer <token>` header.

| Method | Endpoint              | Description         | Access      |
| ------ | --------------------- | ------------------- | ----------- |
| GET    | `/api/employees`      | Get all employees   | USER, ADMIN |
| GET    | `/api/employees/{id}` | Get employee by ID  | USER, ADMIN |
| POST   | `/api/employees`      | Create new employee | ADMIN only  |
| PUT    | `/api/employees/{id}` | Update employee     | ADMIN only  |
| DELETE | `/api/employees/{id}` | Delete employee     | ADMIN only  |

**Employee Object:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com"
}
```

---

## 🔐 Authentication & Authorization

### How It Works

1. **Registration/Login**:
   - User submits credentials to `/api/auth/register` or `/api/auth/login`
   - Backend validates credentials and generates JWT token
   - Token and role are returned to the frontend

2. **Token Storage**:
   - Frontend stores JWT token in `localStorage`
   - Token is included in all subsequent API requests via `Authorization` header

3. **Authorization**:
   - Backend validates JWT token on each protected endpoint
   - Spring Security checks user role and grants/denies access
   - Frontend also hides/shows UI elements based on user role

4. **Token Expiration**:
   - Tokens expire after configured time (e.g., 24 hours)
   - User must login again after expiration
   - Frontend redirects to login page on 401 responses

### Role-Based Access

| Feature               | USER Role | ADMIN Role |
| --------------------- | --------- | ---------- |
| View employee list    | ✅        | ✅         |
| View employee details | ✅        | ✅         |
| Create employee       | ❌        | ✅         |
| Edit employee         | ❌        | ✅         |
| Delete employee       | ❌        | ✅         |

---

## 🔧 Environment Variables

### Required Variables

| Variable                     | Description                | Example                                 |
| ---------------------------- | -------------------------- | --------------------------------------- |
| `POSTGRES_DB`                | Database name              | `employee_db`                           |
| `POSTGRES_USER`              | Database username          | `admin`                                 |
| `POSTGRES_PASSWORD`          | Database password          | `securePass123`                         |
| `SPRING_DATASOURCE_URL`      | JDBC URL                   | `jdbc:postgresql://db:5432/employee_db` |
| `SPRING_DATASOURCE_USERNAME` | Same as POSTGRES_USER      | `admin`                                 |
| `SPRING_DATASOURCE_PASSWORD` | Same as POSTGRES_PASSWORD  | `securePass123`                         |
| `JWT_SECRET`                 | Secret key for JWT signing | Min 256 bits (32+ characters)           |
| `NEXT_PUBLIC_API_URL`        | Backend API base URL       | `http://localhost:8080/api`             |

### Optional Variables

| Variable                        | Description           | Default          |
| ------------------------------- | --------------------- | ---------------- |
| `SERVER_PORT`                   | Backend port          | `8080`           |
| `JWT_EXPIRATION`                | Token expiration (ms) | `86400000` (24h) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode    | `update`         |

---

## 🧪 Testing the Application

### 1. Access the Application

After starting the project:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

### 2. Create an Admin User

**Option A: Via UI**

1. Go to http://localhost:3000/register
2. Fill in the form:
   - Email: `admin@example.com`
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`
3. Click "Registrieren"

**Option B: Via API (cURL)**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "username": "admin",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

### 3. Login and Test Features

1. Login with your admin account
2. Create a few employees
3. Logout and register as a regular USER
4. Login as USER and verify you can only view employees

---

## 🐛 Troubleshooting

### Common Issues

**1. Port already in use**

```
Error: bind: address already in use
```

Solution: Stop the service using the port or change ports in docker-compose files.

**2. Database connection refused**

```
Connection to localhost:5432 refused
```

Solution: Ensure PostgreSQL container is running and ports are correctly mapped.

**3. JWT token invalid**

```
401 Unauthorized
```

Solution: Clear localStorage in browser and login again, or check JWT_SECRET matches between sessions.

### View Logs

```bash
# View all container logs
docker compose logs

# View specific service logs
docker compose logs backend
docker compose logs frontend
docker compose logs db

# Follow logs in real-time
docker compose logs -f
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- **Fadi Salameh** -

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- Next.js Documentation
- Bootstrap Documentation
- Docker Documentation
