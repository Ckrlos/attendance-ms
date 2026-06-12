package cl.duocuc.edutrack.ms.attendance.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceSessionTest {

    static final UUID CLASS_ID   = UUID.randomUUID();
    static final UUID TEACHER_ID = UUID.randomUUID();

    @Test
    void create_setsAllFieldsCorrectly() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);

        assertNotNull(session.getId());
        assertEquals(CLASS_ID,          session.getClassId());
        assertEquals(TEACHER_ID,        session.getTeacherId());
        assertEquals(SessionStatus.OPEN, session.getStatus());
        assertNotNull(session.getSessionDate());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getUpdatedAt());
        assertNull(session.getClosedAt());
        assertNull(session.getDeletedAt());
    }

    @Test
    void create_generatesUniqueIds() {
        AttendanceSession a = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        AttendanceSession b = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void close_transitionsStatusToClosed() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        session.close();
        assertEquals(SessionStatus.CLOSED, session.getStatus());
    }

    @Test
    void close_setsClosedAt() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        session.close();
        assertNotNull(session.getClosedAt());
    }

    @Test
    void close_whenAlreadyClosed_throwsIllegalStateException() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        session.close();
        IllegalStateException ex = assertThrows(IllegalStateException.class, session::close);
        assertTrue(ex.getMessage().contains("CLOSED"));
    }

    @Test
    void validateOpen_whenOpen_doesNotThrow() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        assertDoesNotThrow(session::validateOpen);
    }

    @Test
    void validateOpen_whenClosed_throwsIllegalStateException() {
        AttendanceSession session = AttendanceSession.create(CLASS_ID, TEACHER_ID);
        session.close();
        assertThrows(IllegalStateException.class, session::validateOpen);
    }
}
