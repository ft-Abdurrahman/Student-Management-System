# Security Design

## 1. Authentication Model

Passwords alone are not used for end-user login. Authentication is **OTP-based**, with optional BCrypt-hashed passwords as a backup credential for super-admins / recovery flows.

### 1.1 OTP Lifecycle

1. **Request** — `POST /auth/otp/request` with `{destination, channel}`.
   - Rate-limit: max **3 requests / minute / destination**, **10 / hour / IP**.
   - User row is upserted; previous unconsumed OTPs for the same user/channel are invalidated.
   - 6-digit OTP generated via `SecureRandom`, stored as **BCrypt hash** with `expires_at = now + 5min`.
   - Notification service is invoked (Feign) to deliver the code.
   - Response: `{ otpId, expiresIn: 300 }` — never echoes the code.

2. **Verify** — `POST /auth/otp/verify` with `{otpId, code, deviceId, deviceName, remember}`.
   - `attempts` incremented; **max 5 attempts** before the OTP is locked.
   - On success: row marked `consumed_at`; access + refresh tokens issued.

3. **Resend** — `POST /auth/otp/resend`. 30-second cooldown.

### 1.2 Tokens

| Token | TTL | Algorithm | Storage |
| --- | --- | --- | --- |
| Access  | 15 min | HS256 / HS512 (HMAC) | Client (localStorage / memory) |
| Refresh | 30 days (90 days if "remember me") | Opaque random 256-bit, **BCrypt hash** stored | DB row `refresh_tokens` |

JWT claims:
```json
{
  "sub": "<userId>",
  "email": "...",
  "roles": ["ADMIN"],
  "deviceId": "...",
  "iat": ..., "exp": ...,
  "iss": "sms-auth", "aud": "sms-api"
}
```

### 1.3 Refresh Flow

`POST /auth/refresh {refreshToken}` → looks up by hash, verifies not revoked / expired, issues a new access token and rotates the refresh token (revoke old, insert new).

### 1.4 Device Sessions

Each successful verify creates / updates a `device_sessions` row keyed by `deviceId`. Users can view (`GET /auth/sessions`) and revoke (`DELETE /auth/sessions/{id}`) any session. "Remember device" extends refresh TTL.

## 2. Authorization (RBAC)

Roles: `SUPER_ADMIN`, `ADMIN`, `TEACHER`, `STUDENT`.

| Resource | SUPER_ADMIN | ADMIN | TEACHER | STUDENT |
| --- | :-: | :-: | :-: | :-: |
| Manage admins | RW | – | – | – |
| Students CRUD | RW | RW | R | R (self) |
| Teachers CRUD | RW | RW | R (self) | – |
| Courses / Subjects | RW | RW | R | R |
| Mark attendance | – | RW | RW | R (self) |
| Marks entry | – | RW | RW | R (self) |
| Fee payments | RW | RW | R | RW (self) |
| Notices | RW | RW | RW (own) | R |
| Reports | RW | RW | R (limited) | R (self) |

Enforced via:
- Gateway-level coarse routes
- `@PreAuthorize("hasRole('ADMIN')")` on controller methods
- Domain checks (`student.id == principal.userId`) inside services

## 3. Gateway Hardening

- **CORS** — explicit allow-list (`http://localhost:3000`, configurable per env).
- **Rate limiting** — token-bucket per IP (`200 req/min`) and per-user (`600 req/min`).
- **Request logging** — correlation ID injected (`X-Request-Id`) and propagated.
- **JWT verify** — signature + expiry + issuer + audience; on failure → `401`.
- **Header sanitization** — strips client-supplied `X-User-*` headers and re-injects from validated JWT.

## 4. Application Hardening

| Threat | Mitigation |
| --- | --- |
| SQL injection | JPA parameterized queries, no string concatenation, `@Query` with `:params` |
| XSS | All UI rendering uses `textContent`/escaping helpers; `Content-Security-Policy` header from gateway |
| CSRF | Stateless JWT → CSRF disabled; sensitive admin pages can opt-in to CSRF token if cookie-based |
| Mass assignment | DTOs only (no entity binding); MapStruct or manual mapping |
| Privilege escalation | Roles in JWT signed; gateway re-validates on every request |
| Brute force | OTP attempt cap + IP rate limit + exponential backoff on resend |
| Replay | Short-lived access tokens, refresh-token rotation, single-use OTPs |
| Sensitive data at rest | BCrypt for passwords and OTP codes; secrets via env / Kubernetes Secrets |
| Sensitive data in transit | TLS terminated at ingress / gateway in production |
| Audit | `audit_logs` table records auth events, role grants, admin actions |

## 5. Input Validation

- `jakarta.validation` (`@NotBlank`, `@Email`, `@Pattern`, `@Size`).
- Global `@RestControllerAdvice` translates `MethodArgumentNotValidException` → structured 400 with field-level errors.

## 6. Secrets Management

| Secret | Local | Production |
| --- | --- | --- |
| DB password | `DB_PASSWORD` env var | K8s Secret `sms-db-credentials` |
| JWT signing key | `JWT_SECRET` env var | K8s Secret `sms-jwt` |
| SMTP password | `MAIL_PASSWORD` env var | K8s Secret `sms-smtp` |

`config-server` itself never persists secrets — it references `${ENV_VAR}` placeholders resolved by each service's environment.

## 7. Logging & Audit

- JSON-structured logs via Logback `logstash-logback-encoder` (optional).
- Correlation ID, user ID, role, request path, status, latency on every line.
- `audit_logs` table in `sms_auth` for: OTP request/verify, login, logout, role change, admin CRUD.
