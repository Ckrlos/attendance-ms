package cl.duocuc.edutrack.ms.attendance.repository;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AttendanceSessionRepository implements PanacheRepositoryBase<AttendanceSession, UUID> {

    public Optional<AttendanceSession> findActiveById(UUID id) {
        return find("id = ?1 AND deletedAt IS NULL", id).firstResultOptional();
    }
}
