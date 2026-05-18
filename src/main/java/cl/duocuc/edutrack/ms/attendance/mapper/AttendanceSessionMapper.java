package cl.duocuc.edutrack.ms.attendance.mapper;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AttendanceSessionMapper {

    public SessionResponse toResponse(AttendanceSession session) {
        SessionResponse r = new SessionResponse();
        r.id          = session.getId();
        r.classId     = session.getClassId();
        r.teacherId   = session.getTeacherId();
        r.status      = session.getStatus().name();
        r.sessionDate = session.getSessionDate();
        r.closedAt    = session.getClosedAt();
        r.createdAt   = session.getCreatedAt();
        r.updatedAt   = session.getUpdatedAt();
        return r;
    }
}
