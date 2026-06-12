package cl.duocuc.edutrack.ms.attendance.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceRecordTest {

    static final UUID           STUDENT_ID = UUID.randomUUID();
    static final LocalDateTime  NOW        = LocalDateTime.now();

    AttendanceSession newOpenSession() {
        return AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void create_withAllFields_setsCorrectly() {
        AttendanceSession session = newOpenSession();
        Map<String, Object> meta = Map.of("device", "tablet-1");

        AttendanceRecord record = AttendanceRecord.create(
                session, STUDENT_ID, AttendanceStatus.PRESENT, "MANUAL", meta, NOW);

        assertNotNull(record.getId());
        assertEquals(session,                 record.getSession());
        assertEquals(STUDENT_ID,              record.getStudentId());
        assertEquals(AttendanceStatus.PRESENT, record.getStatus());
        assertEquals("MANUAL",                record.getCaptureMethod());
        assertEquals(meta,                    record.getMetadata());
        assertEquals(NOW,                     record.getRecordedAt());
        assertNotNull(record.getCreatedAt());
        assertNotNull(record.getUpdatedAt());
    }

    @Test
    void create_withOptionalFieldsNull_succeeds() {
        AttendanceRecord record = AttendanceRecord.create(
                newOpenSession(), STUDENT_ID, AttendanceStatus.ABSENT, null, null, NOW);

        assertNull(record.getCaptureMethod());
        assertNull(record.getMetadata());
    }

    @Test
    void create_generatesUniqueIds() {
        AttendanceSession session = newOpenSession();
        AttendanceRecord a = AttendanceRecord.create(session, STUDENT_ID, AttendanceStatus.PRESENT, null, null, NOW);
        AttendanceRecord b = AttendanceRecord.create(session, STUDENT_ID, AttendanceStatus.PRESENT, null, null, NOW);
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void create_withNullSession_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AttendanceRecord.create(null, STUDENT_ID, AttendanceStatus.PRESENT, null, null, NOW));
        assertTrue(ex.getMessage().contains("session"));
    }

    @Test
    void create_withNullStudentId_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AttendanceRecord.create(newOpenSession(), null, AttendanceStatus.PRESENT, null, null, NOW));
        assertTrue(ex.getMessage().contains("studentId"));
    }

    @Test
    void create_withNullStatus_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AttendanceRecord.create(newOpenSession(), STUDENT_ID, null, null, null, NOW));
        assertTrue(ex.getMessage().contains("status"));
    }

    @Test
    void create_withNullRecordedAt_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AttendanceRecord.create(newOpenSession(), STUDENT_ID, AttendanceStatus.PRESENT, null, null, null));
        assertTrue(ex.getMessage().contains("recordedAt"));
    }
}
