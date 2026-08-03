# 🎓 Student Management System

A full-stack **Student Management System** built using a **Microservices Architecture** with **Spring Boot** for the backend and a modern JavaScript frontend. This project demonstrates enterprise-level software design by separating business functionalities into independent services while providing a unified user experience.

---

## 📌 Project Overview

The Student Management System is designed to manage academic operations such as student records, teacher information, courses, attendance, authentication, and notifications. The backend follows the microservices architecture to ensure scalability, maintainability, and independent deployment of services.

---

## 🚀 Features

* User Authentication & Authorization
* Student Management
* Teacher Management
* Course Management
* Attendance Management
* API Gateway for routing requests
* Service Registry for service discovery
* Centralized Configuration Server
* Notification Service
* Responsive Frontend Interface
* RESTful APIs
* Secure JWT-based Authentication
* Modular Microservices Architecture

---

## 🏗️ Architecture

```text
                    Client (Frontend)
                           │
                           ▼
                     API Gateway
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
 Authentication      Student Service     Teacher Service
        │                  │                  │
        ├──────────────┬───┴──────────────┐
        ▼              ▼                  ▼
 Course Service   Attendance Service  Notification Service

             ▲
             │
      Service Registry

             ▲
             │
      Config Server
```

---

## 📂 Project Structure

```text
Student-Management-System/
│
├── frontend/
│
├── api-gateway/
├── config-server/
├── service-registry/
├── authentication-service/
├── student-service/
├── teacher-service/
├── course-service/
├── attendance-service/
├── notification-service/
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

---

## 🛠️ Tech Stack

### Backend

* Java 17+
* Spring Boot
* Spring Cloud
* Spring Security
* Spring Data JPA
* Maven
* REST APIs
* JWT Authentication

### Frontend

* HTML
* CSS
* JavaScript

### Database

* MySQL

### Tools

* IntelliJ IDEA
* Visual Studio Code
* Git
* GitHub
* Postman

---

## ⚙️ Prerequisites

Install the following before running the project:

* Java JDK 17 or later
* Maven
* MySQL
* Git
* IntelliJ IDEA
* Visual Studio Code

---

## ▶️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/Student-Management-System.git
```

### 2. Navigate to the project

```bash
cd Student-Management-System
```

### 3. Configure the database

Create a MySQL database and update the database configuration using your own credentials or environment variables.

### 4. Start the infrastructure services

Run the following services first:

* Config Server
* Service Registry
* API Gateway

### 5. Start the microservices

Run each service individually from IntelliJ IDEA:

* Authentication Service
* Student Service
* Teacher Service
* Course Service
* Attendance Service
* Notification Service

### 6. Start the frontend

Open the `frontend` folder in Visual Studio Code and run it according to your frontend setup.

---

## 📬 API Testing

Use Postman or any REST client to test the available endpoints after all services are running.

---

## 📷 Screenshots

Add screenshots here after deployment.

Example:

```
screenshots/
├── login.png
├── dashboard.png
├── students.png
├── attendance.png
```

---

## 🔒 Security

Sensitive information is **not included** in this repository.

Do **not** commit:

* `.env`
* API Keys
* Database Passwords
* JWT Secrets
* Private Keys

Use environment variables or external configuration instead.

---

## 📈 Future Improvements

* Docker & Docker Compose
* Kubernetes Deployment
* CI/CD Pipeline
* Unit & Integration Testing
* Swagger/OpenAPI Documentation
* Role-Based Access Control
* Email Notifications
* File Upload Support
* Monitoring with Prometheus & Grafana

---

## 🤝 Contributing

Contributions, feature requests, and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push the branch.
5. Open a Pull Request.

---

## 📄 License

This project is intended for educational and portfolio purposes.

---

## 👨‍💻 Author

**Abdurrahman**

If you found this project helpful, consider giving it a ⭐ on GitHub.
