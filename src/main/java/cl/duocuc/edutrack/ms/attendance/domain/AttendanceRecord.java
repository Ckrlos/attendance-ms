package cl.duocuc.edutrack.ms.attendance.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "attendance_records", schema = "attendance")
public class AttendanceRecord {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, updatable = false)
    private AttendanceSession session;

    @Column(name = "student_id", nullable = false, updatable = false)
    private UUID studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10, updatable = false)
    private AttendanceStatus status;

    @Column(name = "capture_method", length = 50, updatable = false)
    private String captureMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> metadata;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected AttendanceRecord() {}

    public static AttendanceRecord create(AttendanceSession session,
                                          UUID studentId,
                                          AttendanceStatus status,
                                          String captureMethod,
                                          Map<String, Object> metadata,
                                          LocalDateTime recordedAt) {
        if (session == null)    throw new IllegalArgumentException("session is required");
        if (studentId == null)  throw new IllegalArgumentException("studentId is required");
        if (status == null)     throw new IllegalArgumentException("status is required");
        if (recordedAt == null) throw new IllegalArgumentException("recordedAt is required");

        AttendanceRecord r = new AttendanceRecord();
        r.id = UUID.randomUUID();
        r.session = session;
        r.studentId = studentId;
        r.status = status;
        r.captureMethod = captureMethod;
        r.metadata = metadata;
        r.recordedAt = recordedAt;
        r.createdAt = LocalDateTime.now();
        r.updatedAt = LocalDateTime.now();
        return r;
    }

    public UUID getId()                    { return id; }
    public AttendanceSession getSession()  { return session; }
    public UUID getStudentId()             { return studentId; }
    public AttendanceStatus getStatus()    { return status; }
    public String getCaptureMethod()       { return captureMethod; }
    public Map<String, Object> getMetadata() { return metadata; }
    public LocalDateTime getRecordedAt()   { return recordedAt; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }
    public LocalDateTime getDeletedAt()    { return deletedAt; }
}
