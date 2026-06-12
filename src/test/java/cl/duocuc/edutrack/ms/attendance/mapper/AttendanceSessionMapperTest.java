package cl.duocuc.edutrack.ms.attendance.mapper;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.domain.SessionStatus;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceSessionMapperTest {

    final AttendanceSessionMapper mapper = new AttendanceSessionMapper();

    @Test
    void toResponse_mapsOpenSessionCorrectly() {
        AttendanceSession session = AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());

        SessionResponse response = mapper.toResponse(session);

        assertEquals(session.getId(),          response.id);
        assertEquals(session.getClassId(),     response.classId);
        assertEquals(session.getTeacherId(),   response.teacherId);
        assertEquals(SessionStatus.OPEN.name(), response.status);
        assertEquals(session.getSessionDate(), response.sessionDate);
        assertEquals(session.getCreatedAt(),   response.createdAt);
        assertEquals(session.getUpdatedAt(),   response.updatedAt);
        assertNull(response.closedAt);
    }

    @Test
    void toResponse_whenClosed_mapsClosedStatusAndClosedAt() {
        AttendanceSession session = AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
        session.close();

        SessionResponse response = mapper.toResponse(session);

        assertEquals(SessionStatus.CLOSED.name(), response.status);
        assertNotNull(response.closedAt);
        assertEquals(session.getClosedAt(), response.closedAt);
    }
}
