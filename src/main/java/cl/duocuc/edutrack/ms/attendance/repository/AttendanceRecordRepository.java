package cl.duocuc.edutrack.ms.attendance.repository;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceRecord;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AttendanceRecordRepository implements PanacheRepositoryBase<AttendanceRecord, UUID> {

    public boolean existsByStudentAndSession(UUID studentId, UUID sessionId) {
        return count("studentId = ?1 AND session.id = ?2 AND deletedAt IS NULL", studentId, sessionId) > 0;
    }

    public List<AttendanceRecord> findBySession(UUID sessionId) {
        return list("session.id = ?1 AND deletedAt IS NULL", sessionId);
    }
}
