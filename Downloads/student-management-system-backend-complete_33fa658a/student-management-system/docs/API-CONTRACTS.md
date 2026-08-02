# API Contracts (v1)

All endpoints are exposed via the API Gateway at `http://localhost:8080`. Authentication uses `Authorization: Bearer <JWT>` except where noted.

Response envelope:
```json
{ "status": "SUCCESS|ERROR", "message": "...", "timestamp": "ISO-8601", "data": {}, "errors": [] }
```

## Authentication Service `/api/v1/auth`

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| POST | `/otp/request` | none | Body `{ "destination": "email or phone", "channel": "EMAIL|SMS" }` → sends OTP |
| POST | `/otp/verify` | none | Body `{ "otpId", "code", "deviceId", "deviceName", "remember": false }` → returns tokens |
| POST | `/otp/resend` | none | Body `{ "otpId" }` (30s cooldown) |
| POST | `/refresh` | none | Body `{ "refreshToken" }` → new access token |
| POST | `/logout` | bearer | Revokes refresh token + device session |
| GET  | `/me` | bearer | Current user profile and roles |
| GET  | `/sessions` | bearer | List active device sessions |
| DELETE | `/sessions/{id}` | bearer | Revoke a session |

## Notification Service `/api/v1/notifications` (internal-only via gateway ACL)

| Method | Path | Description |
| --- | --- | --- |
| POST | `/email` | Send templated email |
| POST | `/otp/send` | Generate + send OTP via channel |
| POST | `/otp/verify` | Verify OTP (called by auth-service) |

## Student Service `/api/v1/students`

| Method | Path | Roles | Description |
| --- | --- | --- | --- |
| GET | `/` | ADMIN, TEACHER | Paginated list `?page=&size=&sort=&q=&department=&semester=&status=` |
| GET | `/{id}` | ADMIN, TEACHER, owner STUDENT | Detail |
| POST | `/` | ADMIN | Create |
| PUT | `/{id}` | ADMIN | Update |
| DELETE | `/{id}` | ADMIN | Soft delete |
| GET | `/{id}/export.pdf` | ADMIN | PDF profile |
| GET | `/export.xlsx` | ADMIN | Excel list |

## Teacher Service `/api/v1/teachers`

Same CRUD pattern. Extra:
- `GET /{id}/subjects` — subjects assigned
- `GET /{id}/timetable` — teacher timetable (Feign → timetable-service)

## Course Service `/api/v1/courses` and `/api/v1/departments`

- Departments CRUD (`/departments`)
- Courses CRUD (`/courses`)
- `GET /departments/{id}/courses` — courses of a department

## Subject Service `/api/v1/subjects`

- CRUD
- `POST /{id}/assign-teacher` body `{ "teacherId": 12 }`
- `GET ?courseId=&semester=`

## Attendance Service `/api/v1/attendance`

- `POST /mark` body `[{ "studentId", "subjectId", "date", "status" }, ...]`
- `GET /student/{id}?from=&to=&subjectId=`
- `GET /student/{id}/percentage?subjectId=`
- `GET /report/monthly?month=YYYY-MM&courseId=&semester=`
- `POST /leave` — student leave request
- `PUT /leave/{id}/approve` — TEACHER/ADMIN

## Exam Service `/api/v1/exams`

- CRUD exams
- `POST /{id}/schedule` — add exam-subject schedule
- `GET /{id}/schedule` — list

## Result Service `/api/v1/results`

- `POST /marks` — bulk marks entry
- `GET /student/{id}` — all results
- `GET /student/{id}/gpa` — semester GPA + CGPA
- `GET /student/{id}/transcript.pdf` — PDF transcript

## Fee Service `/api/v1/fees`

- `GET /structure?courseId=&semester=`
- `GET /student/{id}` — fee status
- `POST /pay` — initiate payment (returns gateway redirect URL — abstract `PaymentProvider`)
- `GET /student/{id}/receipts` — list
- `GET /receipts/{id}.pdf` — receipt PDF

## Notice Service `/api/v1/notices`

- CRUD with `?audience=&departmentId=&pinned=`
- `POST /{id}/pin` and `DELETE /{id}/pin`

## Timetable Service `/api/v1/timetable`

- `GET /student/{id}` — weekly
- `GET /teacher/{id}` — weekly
- `POST /slots` — admin creates slot
- `PUT /slots/{id}` / `DELETE /slots/{id}`

## Report Service `/api/v1/reports`

- `POST /generate` body `{ "type": "STUDENT|ATTENDANCE|RESULT|FEE|TEACHER|DEPARTMENT", "filters": {...}, "format": "PDF|XLSX" }`
- `GET /{id}/download`

## Admin Dashboard Service `/api/v1/dashboard`

- `GET /summary` — aggregate counts (students, teachers, today's attendance, revenue, pending fees, upcoming exams)
- `GET /charts/admissions?range=12M`
- `GET /charts/attendance?range=30D`
- `GET /charts/revenue?range=12M`
- `GET /recent-admissions?limit=10`
