# System Architecture

## 1. High-Level Architecture

```mermaid
flowchart TB
    subgraph Client["Client Layer"]
        Browser["Browser (HTML/CSS/JS)"]
        Mobile["Mobile (future)"]
    end

    subgraph Edge["Edge / Routing"]
        GW["API Gateway<br/>(Spring Cloud Gateway)<br/>:8080<br/>JWT verify - CORS - rate limit"]
    end

    subgraph Platform["Platform Services"]
        CS["Config Server<br/>:8888"]
        SR["Eureka Registry<br/>:8761"]
    end

    subgraph Core["Core Business Microservices"]
        AUTH["Authentication<br/>:9001"]
        NOTIF["Notification<br/>:9002"]
        STU["Student<br/>:9003"]
        TEA["Teacher<br/>:9004"]
        CRS["Course<br/>:9005"]
        SUB["Subject<br/>:9006"]
        ATT["Attendance<br/>:9007"]
        EXM["Exam<br/>:9008"]
        RES["Result<br/>:9009"]
        FEE["Fee<br/>:9010"]
        NOT["Notice<br/>:9011"]
        TT["Timetable<br/>:9012"]
        RPT["Report<br/>:9013"]
        DSH["Admin Dashboard<br/>:9014"]
    end

    subgraph Data["Data Layer (DB per service)"]
        DB_AUTH[(sms_auth)]
        DB_STU[(sms_student)]
        DB_TEA[(sms_teacher)]
        DB_CRS[(sms_course)]
        DB_SUB[(sms_subject)]
        DB_ATT[(sms_attendance)]
        DB_EXM[(sms_exam)]
        DB_RES[(sms_result)]
        DB_FEE[(sms_fee)]
        DB_NOT[(sms_notice)]
        DB_TT[(sms_timetable)]
        DB_RPT[(sms_report)]
    end

    Browser --> GW
    Mobile --> GW
    GW --> AUTH & STU & TEA & CRS & SUB & ATT & EXM & RES & FEE & NOT & TT & RPT & DSH
    AUTH --> NOTIF
    AUTH --> DB_AUTH
    STU --> DB_STU
    TEA --> DB_TEA
    CRS --> DB_CRS
    SUB --> DB_SUB
    ATT --> DB_ATT
    EXM --> DB_EXM
    RES --> DB_RES
    FEE --> DB_FEE
    NOT --> DB_NOT
    TT --> DB_TT
    RPT --> DB_RPT

    AUTH -.register/discover.-> SR
    NOTIF -.-> SR
    STU -.-> SR
    GW -.-> SR
    CS -.config.-> AUTH & NOTIF & STU & TEA & GW
```

## 2. Authentication Flow (OTP + JWT)

```mermaid
sequenceDiagram
    autonumber
    participant U as User Browser
    participant GW as API Gateway
    participant A as Auth Service
    participant N as Notification Service
    participant DB as sms_auth DB

    U->>GW: POST /api/v1/auth/otp/request {email}
    GW->>A: forward
    A->>DB: rate-limit check + persist OTP (hashed, 5 min TTL)
    A->>N: send-otp(email, code) via Feign
    N-->>U: Email with 6-digit OTP
    A-->>U: 200 {otpId, expiresIn:300}

    U->>GW: POST /api/v1/auth/otp/verify {otpId, code, deviceId}
    GW->>A: forward
    A->>DB: verify hash, attempts <= 5, not expired
    A-->>U: 200 {accessToken, refreshToken, user, roles}

    Note over U,A: Subsequent requests carry Bearer JWT
    U->>GW: GET /api/v1/students  (Authorization: Bearer ...)
    GW->>GW: validate JWT signature + expiry
    GW->>STU: forward with X-User-Id, X-Roles headers
    STU-->>U: data
```

## 3. Request Lifecycle

1. Client hits **API Gateway** (`:8080`).
2. Gateway runs filters: CORS, request logging, rate limiting (Redis token-bucket optional), JWT validation.
3. On valid JWT, Gateway injects `X-User-Id`, `X-User-Email`, `X-Roles` headers and routes via Eureka service IDs (e.g. `lb://student-service`).
4. Downstream service reads identity from headers, applies RBAC at controller/method level.
5. Cross-service calls use OpenFeign (client-side load-balanced via Spring Cloud LoadBalancer).
6. Each service publishes health (`/actuator/health`) and metrics (`/actuator/prometheus`).

## 4. Centralized Configuration

- `config-server` reads from a local filesystem backend at `config-server/src/main/resources/config/`.
- Each service has `bootstrap.yml` pointing to `http://localhost:8888`.
- Per-service configs: `application-{service}.yml` (e.g. `application-student-service.yml`).
- Sensitive values come from environment variables; `${DB_PASSWORD}`, `${JWT_SECRET}`, `${MAIL_PASSWORD}`.

## 5. Service Discovery

- `service-registry` runs Eureka on `:8761`.
- All other services declare `eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/`.
- Gateway routes use `lb://<service-id>` for client-side load balancing.

## 6. Resilience & Cross-Cutting Concerns

| Concern | Mechanism |
| --- | --- |
| Retries / circuit breaking | Resilience4j on Feign clients |
| Rate limiting | Gateway filter (in-memory token bucket; Redis optional) |
| Tracing | Micrometer Tracing + W3C `traceparent` headers |
| Logging | JSON logs via Logback + correlation IDs |
| Metrics | Actuator + Prometheus endpoint |
| Validation | `jakarta.validation` annotations + global `@ControllerAdvice` |
| Exceptions | Standardized error response envelope |

## 7. Standard Response Envelope

```json
{
  "status": "SUCCESS",
  "message": "Student created",
  "timestamp": "2026-06-25T10:15:30Z",
  "data": { "id": 42, "rollNumber": "CSE-2026-042" },
  "errors": null
}
```

## 8. Folder Layout

```
student-management-system/
├── docs/                  # Architecture, DB, API, security docs
├── frontend/              # HTML / CSS / vanilla JS UI
├── config-server/         # Spring Cloud Config
├── service-registry/      # Eureka
├── api-gateway/           # Spring Cloud Gateway
├── authentication-service/
├── notification-service/
├── student-service/
├── teacher-service/
├── course-service/
├── subject-service/
├── attendance-service/
├── exam-service/
├── result-service/
├── fee-service/
├── notice-service/
├── timetable-service/
├── report-service/
├── admin-dashboard-service/
├── docker/                # Dockerfiles, compose, init SQL
└── kubernetes/            # Manifests
```
