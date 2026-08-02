# Student Management System (SMS)

An enterprise-grade, microservices-based University ERP built with Spring Boot 3.x, Spring Cloud, MySQL, and a vanilla HTML/CSS/JS frontend.

## Highlights

- 17 microservices following the Database-per-Service pattern
- OTP-based authentication (Email + Mobile) with JWT access/refresh tokens
- Spring Cloud: Config Server, Eureka, Gateway, OpenFeign, Load Balancer
- Role-Based Access Control: `SUPER_ADMIN`, `ADMIN`, `TEACHER`, `STUDENT`
- Centralized configuration, health checks, metrics, request tracing
- Production-ready Dockerfiles, docker-compose, and Kubernetes manifests
- Modern responsive UI (no frameworks) with light/dark mode

## Documentation

| File | Purpose |
| --- | --- |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | System diagrams, service interactions, request flow |
| [docs/DATABASE.md](docs/DATABASE.md) | Per-service ER diagrams and seed data |
| [docs/API-CONTRACTS.md](docs/API-CONTRACTS.md) | REST endpoints for every service |
| [docs/SECURITY.md](docs/SECURITY.md) | OTP + JWT flow, RBAC matrix, threat mitigations |
| [docs/INSTALLATION.md](docs/INSTALLATION.md) | Local setup, MySQL bootstrap, env vars |
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | Docker, Kubernetes, production checklist |

## Quick start (local)

```bash
# 1. Create databases
mysql -u root -p < docker/init/00-create-databases.sql

# 2. Export environment variables
export DB_USERNAME=root
export DB_PASSWORD=<your-db-password>
export JWT_SECRET=change-me-in-production-please-use-256-bit
export MAIL_USERNAME=//write gmail here
export MAIL_PASSWORD=#your-gmail-app-password

# 3. Boot infrastructure
(cd config-server     && ./mvnw spring-boot:run) &
(cd service-registry  && ./mvnw spring-boot:run) &
(cd api-gateway       && ./mvnw spring-boot:run) &

# 4. Boot business services
(cd notification-service   && ./mvnw spring-boot:run) &
(cd authentication-service && ./mvnw spring-boot:run) &
(cd student-service        && ./mvnw spring-boot:run) &
# ... etc

# 5. Frontend
cd frontend && python3 -m http.server 3000
```

## Default Ports

| Service | Port |
| --- | --- |
| Config Server | 8888 |
| Service Registry (Eureka) | 8761 |
| API Gateway | 8080 |
| Authentication Service | 9001 |
| Notification Service | 9002 |
| Student Service | 9003 |
| Teacher Service | 9004 |
| Course Service | 9005 |
| Subject Service | 9006 |
| Attendance Service | 9007 |
| Exam Service | 9008 |
| Result Service | 9009 |
| Fee Service | 9010 |
| Notice Service | 9011 |
| Timetable Service | 9012 |
| Report Service | 9013 |
| Admin Dashboard Service | 9014 |
| Frontend (dev) | 3000 |
