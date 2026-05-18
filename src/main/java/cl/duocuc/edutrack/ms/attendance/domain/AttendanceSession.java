package cl.duocuc.edutrack.ms.attendance.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_sessions", schema = "attendance")
public class AttendanceSession {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private SessionStatus status;

    @Column(name = "session_date", nullable = false)
    private LocalDateTime sessionDate;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected AttendanceSession() {}

    public static AttendanceSession create(UUID classId, UUID teacherId) {
        AttendanceSession s = new AttendanceSession();
        s.id = UUID.randomUUID();
        s.classId = classId;
        s.teacherId = teacherId;
        s.status = SessionStatus.OPEN;
        s.sessionDate = LocalDateTime.now();
        s.createdAt = LocalDateTime.now();
        s.updatedAt = LocalDateTime.now();
        return s;
    }

    public void close() {
        if (this.status == SessionStatus.CLOSED) {
            throw new IllegalStateException("Session is already CLOSED");
        }
        this.status = SessionStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOpen() {
        if (this.status == SessionStatus.CLOSED) {
            throw new IllegalStateException("Session is CLOSED");
        }
    }

    public UUID getId()               { return id; }
    public UUID getClassId()          { return classId; }
    public UUID getTeacherId()        { return teacherId; }
    public SessionStatus getStatus()  { return status; }
    public LocalDateTime getSessionDate() { return sessionDate; }
    public LocalDateTime getClosedAt()    { return closedAt; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }
    public LocalDateTime getDeletedAt()   { return deletedAt; }
}
