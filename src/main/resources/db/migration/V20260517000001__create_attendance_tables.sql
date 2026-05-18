CREATE TABLE IF NOT EXISTS attendance.attendance_sessions (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id     UUID        NOT NULL,
    teacher_id   UUID        NOT NULL,
    status       VARCHAR(10) NOT NULL DEFAULT 'OPEN',
    session_date TIMESTAMP   NOT NULL DEFAULT NOW(),
    closed_at    TIMESTAMP   NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMP   NULL,
    CONSTRAINT chk_session_status CHECK (status IN ('OPEN', 'CLOSED'))
);

CREATE TABLE IF NOT EXISTS attendance.attendance_records (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id     UUID        NOT NULL REFERENCES attendance.attendance_sessions(id),
    student_id     UUID        NOT NULL,
    status         VARCHAR(10) NOT NULL,
    capture_method VARCHAR(50) NULL,
    metadata       JSONB       NULL,
    recorded_at    TIMESTAMP   NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP   NULL,
    CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT', 'ABSENT', 'JUSTIFIED')),
    CONSTRAINT uq_student_session UNIQUE (student_id, session_id)
);

CREATE INDEX IF NOT EXISTS idx_sessions_class_id   ON attendance.attendance_sessions(class_id);
CREATE INDEX IF NOT EXISTS idx_records_session_id  ON attendance.attendance_records(session_id);
CREATE INDEX IF NOT EXISTS idx_records_student_id  ON attendance.attendance_records(student_id);
