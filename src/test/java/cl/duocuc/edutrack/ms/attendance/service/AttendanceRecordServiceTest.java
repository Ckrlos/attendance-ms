package cl.duocuc.edutrack.ms.attendance.service;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceRecord;
import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.domain.AttendanceStatus;
import cl.duocuc.edutrack.ms.attendance.dto.request.CreateRecordRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.RecordResponse;
import cl.duocuc.edutrack.ms.attendance.mapper.AttendanceRecordMapper;
import cl.duocuc.edutrack.ms.attendance.repository.AttendanceRecordRepository;
import cl.duocuc.edutrack.ms.infrastructure.exception.ConflictException;
import cl.duocuc.edutrack.ms.infrastructure.exception.ForbiddenException;
import cl.duocuc.edutrack.ms.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceRecordServiceTest {

    @Mock AttendanceRecordRepository  recordRepository;
    @Mock AttendanceSessionService    sessionService;
    @Mock AttendanceRecordMapper      recordMapper;

    @InjectMocks AttendanceRecordService service;

    final UUID sessionId = UUID.randomUUID();
    final UUID studentId = UUID.randomUUID();

    AttendanceSession openSession() {
        return AttendanceSession.create(UUID.randomUUID(), UUID.randomUUID());
    }

    CreateRecordRequest validRequest() {
        CreateRecordRequest req = new CreateRecordRequest();
        req.studentId  = studentId;
        req.status     = "PRESENT";
        req.recordedAt = LocalDateTime.now();
        return req;
    }

    // ── registerRecord ────────────────────────────────────────────────────────

    @Test
    void registerRecord_persistsRecord() {
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(false);
        when(recordMapper.toResponse(any())).thenReturn(new RecordResponse());

        service.registerRecord(sessionId, validRequest());

        verify(recordRepository).persist(any(AttendanceRecord.class));
    }

    @Test
    void registerRecord_returnsWrappedResponse() {
        RecordResponse recordResponse = new RecordResponse();
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(false);
        when(recordMapper.toResponse(any())).thenReturn(recordResponse);

        ApiResponse<RecordResponse> result = service.registerRecord(sessionId, validRequest());

        assertEquals(recordResponse, result.getData());
    }

    @Test
    void registerRecord_persistsRecordWithCorrectStudentAndStatus() {
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(false);
        when(recordMapper.toResponse(any())).thenReturn(new RecordResponse());
        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);

        service.registerRecord(sessionId, validRequest());

        verify(recordRepository).persist(captor.capture());
        assertEquals(studentId,                captor.getValue().getStudentId());
        assertEquals(AttendanceStatus.PRESENT, captor.getValue().getStatus());
    }

    @Test
    void registerRecord_withMetadataAndCaptureMethod_persistsCorrectly() {
        CreateRecordRequest req = validRequest();
        req.captureMethod = "BIOMETRIC";
        req.metadata      = Map.of("sensor", "fprint-01");
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(false);
        when(recordMapper.toResponse(any())).thenReturn(new RecordResponse());
        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);

        service.registerRecord(sessionId, req);

        verify(recordRepository).persist(captor.capture());
        assertEquals("BIOMETRIC", captor.getValue().getCaptureMethod());
        assertEquals(Map.of("sensor", "fprint-01"), captor.getValue().getMetadata());
    }

    @Test
    void registerRecord_whenDuplicate_throwsConflictException() {
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.registerRecord(sessionId, validRequest()));
        verify(recordRepository, never()).persist(any(AttendanceRecord.class));
    }

    @Test
    void registerRecord_withInvalidStatus_throwsIllegalArgumentException() {
        CreateRecordRequest req = validRequest();
        req.status = "TARDY";
        when(sessionService.getOpenSession(sessionId)).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(studentId, sessionId)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registerRecord(sessionId, req));
        assertTrue(ex.getMessage().contains("Invalid status"));
        verify(recordRepository, never()).persist(any(AttendanceRecord.class));
    }

    @Test
    void registerRecord_allValidStatuses_doNotThrow() {
        when(sessionService.getOpenSession(any())).thenReturn(openSession());
        when(recordRepository.existsByStudentAndSession(any(), any())).thenReturn(false);
        when(recordMapper.toResponse(any())).thenReturn(new RecordResponse());

        for (AttendanceStatus status : AttendanceStatus.values()) {
            CreateRecordRequest req = new CreateRecordRequest();
            req.studentId  = UUID.randomUUID();
            req.status     = status.name();
            req.recordedAt = LocalDateTime.now();
            assertDoesNotThrow(() -> service.registerRecord(UUID.randomUUID(), req));
        }
    }

    @Test
    void registerRecord_whenSessionNotFound_throwsNotFoundException() {
        when(sessionService.getOpenSession(sessionId)).thenThrow(new NotFoundException("", "Session not found"));

        assertThrows(NotFoundException.class, () -> service.registerRecord(sessionId, validRequest()));
    }

    @Test
    void registerRecord_whenSessionClosed_throwsForbiddenException() {
        when(sessionService.getOpenSession(sessionId)).thenThrow(new ForbiddenException("", "Session is CLOSED"));

        assertThrows(ForbiddenException.class, () -> service.registerRecord(sessionId, validRequest()));
    }
}
