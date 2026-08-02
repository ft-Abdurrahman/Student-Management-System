# Database Design (Database-per-Service)

A single MySQL 8.x instance hosts one **schema** per service. There are **no cross-schema foreign keys** — referential integrity across services is enforced at the application layer using IDs (and Feign calls when stricter consistency is needed).

> Credentials are loaded from environment variables. Local dev defaults:
> `host=localhost`, `port=3306`, `user=root`, `password=<your-db-password>`.

## Schemas

| Schema | Owner Service |
| --- | --- |
| `sms_auth` | authentication-service |
| `sms_notification` | notification-service |
| `sms_student` | student-service |
| `sms_teacher` | teacher-service |
| `sms_course` | course-service |
| `sms_subject` | subject-service |
| `sms_attendance` | attendance-service |
| `sms_exam` | exam-service |
| `sms_result` | result-service |
| `sms_fee` | fee-service |
| `sms_notice` | notice-service |
| `sms_timetable` | timetable-service |
| `sms_report` | report-service |

## 1. `sms_auth`

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    USERS ||--o{ OTP_CODES : requests
    USERS ||--o{ REFRESH_TOKENS : issued
    USERS ||--o{ DEVICE_SESSIONS : owns
    USERS ||--o{ AUDIT_LOGS : produces

    USERS {
        bigint id PK
        varchar email UK
        varchar phone UK
        varchar password_hash "bcrypt, nullable"
        varchar full_name
        boolean enabled
        boolean email_verified
        boolean phone_verified
        datetime created_at
        datetime updated_at
    }
    USER_ROLES {
        bigint id PK
        bigint user_id FK
        varchar role "SUPER_ADMIN|ADMIN|TEACHER|STUDENT"
    }
    OTP_CODES {
        bigint id PK
        bigint user_id FK
        varchar channel "EMAIL|SMS"
        varchar code_hash
        int attempts
        datetime expires_at
        datetime consumed_at
        datetime created_at
    }
    REFRESH_TOKENS {
        bigint id PK
        bigint user_id FK
        varchar token_hash UK
        varchar device_id
        datetime expires_at
        datetime revoked_at
    }
    DEVICE_SESSIONS {
        bigint id PK
        bigint user_id FK
        varchar device_id
        varchar device_name
        varchar ip_address
        varchar user_agent
        boolean remembered
        datetime last_seen_at
    }
    AUDIT_LOGS {
        bigint id PK
        bigint user_id FK
        varchar action
        varchar ip_address
        text details
        datetime created_at
    }
```

## 2. `sms_student`

```mermaid
erDiagram
    STUDENTS {
        bigint id PK
        varchar roll_number UK
        varchar admission_number UK
        varchar first_name
        varchar last_name
        date dob
        char gender
        varchar email UK
        varchar phone
        varchar blood_group
        varchar photo_url
        bigint department_id "logical FK to sms_course.departments"
        bigint course_id "logical FK"
        int semester
        varchar status "ACTIVE|INACTIVE|GRADUATED|SUSPENDED"
        datetime created_at
        datetime updated_at
    }
    PARENTS ||--|| STUDENTS : belongs_to
    PARENTS {
        bigint id PK
        bigint student_id FK
        varchar father_name
        varchar father_phone
        varchar mother_name
        varchar mother_phone
        varchar guardian_name
        varchar guardian_phone
    }
    ADDRESSES ||--|| STUDENTS : belongs_to
    ADDRESSES {
        bigint id PK
        bigint student_id FK
        varchar line1
        varchar line2
        varchar city
        varchar state
        varchar country
        varchar postal_code
    }
```

## 3. `sms_teacher`

```mermaid
erDiagram
    TEACHERS {
        bigint id PK
        varchar employee_code UK
        varchar first_name
        varchar last_name
        varchar email UK
        varchar phone
        bigint department_id
        varchar qualification
        int experience_years
        decimal salary
        varchar status
        datetime created_at
    }
    TEACHER_SUBJECTS {
        bigint id PK
        bigint teacher_id FK
        bigint subject_id "logical FK"
    }
```

## 4. `sms_course`

```mermaid
erDiagram
    DEPARTMENTS ||--o{ COURSES : offers
    DEPARTMENTS {
        bigint id PK
        varchar code UK
        varchar name
        bigint head_teacher_id "logical FK"
    }
    COURSES {
        bigint id PK
        varchar code UK
        varchar name
        int credits
        int duration_semesters
        bigint department_id FK
    }
```

## 5. `sms_subject`

```mermaid
erDiagram
    SUBJECTS {
        bigint id PK
        varchar code UK
        varchar name
        int credits
        int semester
        bigint course_id "logical FK"
        bigint teacher_id "logical FK"
    }
```

## 6. `sms_attendance`

```mermaid
erDiagram
    ATTENDANCE {
        bigint id PK
        bigint student_id
        bigint subject_id
        date attendance_date
        varchar status "PRESENT|ABSENT|LATE|LEAVE"
        bigint marked_by_teacher_id
        datetime created_at
    }
    LEAVE_REQUESTS {
        bigint id PK
        bigint student_id
        date from_date
        date to_date
        text reason
        varchar status "PENDING|APPROVED|REJECTED"
    }
```

## 7. `sms_exam`

```mermaid
erDiagram
    EXAMS {
        bigint id PK
        varchar name
        varchar type "MID|END|QUIZ|PRACTICAL"
        bigint course_id
        int semester
        date start_date
        date end_date
    }
    EXAM_SCHEDULES {
        bigint id PK
        bigint exam_id FK
        bigint subject_id
        date exam_date
        time start_time
        time end_time
        varchar hall
        bigint invigilator_teacher_id
    }
```

## 8. `sms_result`

```mermaid
erDiagram
    RESULTS {
        bigint id PK
        bigint student_id
        bigint exam_id
        bigint subject_id
        decimal marks_obtained
        decimal max_marks
        varchar grade
        decimal gpa
    }
    SEMESTER_RESULTS {
        bigint id PK
        bigint student_id
        int semester
        decimal sgpa
        decimal cgpa
        varchar status "PASS|FAIL"
    }
```

## 9. `sms_fee`

```mermaid
erDiagram
    FEE_CATEGORIES ||--o{ FEE_STRUCTURES : has
    FEE_CATEGORIES {
        bigint id PK
        varchar name
        text description
    }
    FEE_STRUCTURES {
        bigint id PK
        bigint category_id FK
        bigint course_id
        int semester
        decimal amount
        date due_date
    }
    FEE_PAYMENTS {
        bigint id PK
        bigint student_id
        bigint fee_structure_id FK
        decimal amount_paid
        varchar method "ONLINE|CASH|CHEQUE"
        varchar transaction_id
        varchar status "PAID|PENDING|FAILED|REFUNDED"
        datetime paid_at
    }
```

## 10. `sms_notice`

```mermaid
erDiagram
    NOTICES {
        bigint id PK
        varchar title
        text body
        varchar audience "ALL|STUDENT|TEACHER|DEPARTMENT"
        bigint department_id "nullable"
        boolean pinned
        datetime publish_at
        datetime expires_at
        bigint created_by
    }
```

## 11. `sms_timetable`

```mermaid
erDiagram
    TIMETABLE_SLOTS {
        bigint id PK
        bigint course_id
        int semester
        bigint subject_id
        bigint teacher_id
        varchar day_of_week
        time start_time
        time end_time
        varchar classroom
    }
```

## 12. `sms_notification`

```mermaid
erDiagram
    EMAIL_LOG {
        bigint id PK
        varchar to_address
        varchar subject
        varchar template
        varchar status "SENT|FAILED|QUEUED"
        text error
        datetime sent_at
    }
    OTP_LOG {
        bigint id PK
        varchar destination
        varchar channel
        varchar status
        datetime created_at
    }
```

## Seed Data

Each service's `src/main/resources/db/migration/V2__seed_data.sql` inserts a minimal demo dataset:

- 1 SUPER_ADMIN: `admin@sms.edu`
- 1 ADMIN: `registrar@sms.edu`
- 2 TEACHERS, 5 STUDENTS
- 3 DEPARTMENTS (CSE, ECE, ME)
- 4 COURSES, 12 SUBJECTS
- Sample timetable, fee structure, notices

## Bootstrap SQL

See `docker/init/00-create-databases.sql` for the schema-creation script.
