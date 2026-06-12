package cl.duocuc.edutrack.ms.attendance.mapper;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceRecord;
import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.domain.AttendanceStatus;
import cl.duocuc.edutrack.ms.attendance.dto.response.RecordResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceRecordMapperTest {

    final AttendanceRecordMapper mapper = new AttendanceRecordMapper();

    @Test
    void toResponse_mapsAllFieldsCorrectly() {
        AttendanceSession session  = AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
        UUID studentId             = UUID.randomUUID();
        LocalDateTime recordedAt   = LocalDateTime.now();
        Map<String, Object> meta   = Map.of("device", "tablet-aula-3");

        AttendanceRecord record = AttendanceRecord.create(
                session, studentId, AttendanceStatus.PRESENT, "MANUAL", meta, recordedAt);

        RecordResponse response = mapper.toResponse(record);

        assertEquals(record.getId(),              response.id);
        assertEquals(session.getId(),             response.sessionId);
        assertEquals(studentId,                   response.studentId);
        assertEquals(AttendanceStatus.PRESENT.name(), response.status);
        assertEquals("MANUAL",                    response.captureMethod);
        assertEquals(meta,                        response.metadata);
        assertEquals(recordedAt,                  response.recordedAt);
        assertNotNull(response.createdAt);
    }

    @Test
    void toResponse_withNullOptionals_setsNulls() {
        AttendanceSession session = AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
        AttendanceRecord record = AttendanceRecord.create(
                session, UUID.randomUUID(), AttendanceStatus.ABSENT, null, null, LocalDateTime.now());

        RecordResponse response = mapper.toResponse(record);

        assertNull(response.captureMethod);
        assertNull(response.metadata);
    }

    @Test
    void toResponse_mapsAbsentAndJustifiedStatuses() {
        AttendanceSession session = AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord absent = AttendanceRecord.create(
                session, UUID.randomUUID(), AttendanceStatus.ABSENT, null, null, now);
        AttendanceRecord justified = AttendanceRecord.create(
                session, UUID.randomUUID(), AttendanceStatus.JUSTIFIED, null, null, now);

        assertEquals("ABSENT",    mapper.toResponse(absent).status);
        assertEquals("JUSTIFIED", mapper.toResponse(justified).status);
    }
}
