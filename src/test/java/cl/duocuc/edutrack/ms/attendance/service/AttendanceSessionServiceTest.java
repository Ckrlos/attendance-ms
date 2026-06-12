package cl.duocuc.edutrack.ms.attendance.service;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.domain.SessionStatus;
import cl.duocuc.edutrack.ms.attendance.dto.request.CreateSessionRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import cl.duocuc.edutrack.ms.attendance.mapper.AttendanceSessionMapper;
import cl.duocuc.edutrack.ms.attendance.repository.AttendanceSessionRepository;
import cl.duocuc.edutrack.ms.infrastructure.context.RequestContext;
import cl.duocuc.edutrack.ms.infrastructure.context.RequestHeaders;
import cl.duocuc.edutrack.ms.infrastructure.exception.ForbiddenException;
import cl.duocuc.edutrack.ms.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AttendanceSessionServiceTest {

    @Mock AttendanceSessionRepository sessionRepository;
    @Mock AttendanceSessionMapper      sessionMapper;
    @Mock RequestContext               requestContext;

    @InjectMocks AttendanceSessionService service;

    final UUID teacherId = UUID.randomUUID();
    final UUID classId   = UUID.randomUUID();
    final UUID sessionId = UUID.randomUUID();

    @BeforeEach
    void stubRequestContext() {
        RequestHeaders headers = new RequestHeaders(Optional.of(teacherId), List.of());
        lenient().when(requestContext.headers()).thenReturn(headers);
    }

    // ── createSession ──────────────────────────────────────────────────────────

    @Test
    void createSession_persistsSession() {
        CreateSessionRequest req = new CreateSessionRequest();
        req.classId = classId;
        when(sessionMapper.toResponse(any())).thenReturn(new SessionResponse());

        service.createSession(req);

        verify(sessionRepository).persist(any(AttendanceSession.class));
    }

    @Test
    void createSession_usesTeacherIdFromContext() {
        CreateSessionRequest req = new CreateSessionRequest();
        req.classId = classId;
        ArgumentCaptor<AttendanceSession> captor = ArgumentCaptor.forClass(AttendanceSession.class);
        when(sessionMapper.toResponse(any())).thenReturn(new SessionResponse());

        service.createSession(req);

        verify(sessionRepository).persist(captor.capture());
        assertEquals(teacherId, captor.getValue().getTeacherId());
    }

    @Test
    void createSession_returnsWrappedResponse() {
        CreateSessionRequest req = new CreateSessionRequest();
        req.classId = classId;
        SessionResponse sessionResponse = new SessionResponse();
        when(sessionMapper.toResponse(any())).thenReturn(sessionResponse);

        ApiResponse<SessionResponse> result = service.createSession(req);

        assertEquals(sessionResponse, result.getData());
    }

    // ── closeSession ──────────────────────────────────────────────────────────

    @Test
    void closeSession_closesOpenSession() {
        AttendanceSession session = AttendanceSession.create(classId, teacherId);
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.of(session));
        when(sessionMapper.toResponse(session)).thenReturn(new SessionResponse());

        service.closeSession(sessionId);

        assertEquals(SessionStatus.CLOSED, session.getStatus());
    }

    @Test
    void closeSession_returnsWrappedResponse() {
        AttendanceSession session = AttendanceSession.create(classId, teacherId);
        SessionResponse sessionResponse = new SessionResponse();
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.of(session));
        when(sessionMapper.toResponse(session)).thenReturn(sessionResponse);

        ApiResponse<SessionResponse> result = service.closeSession(sessionId);

        assertEquals(sessionResponse, result.getData());
    }

    @Test
    void closeSession_whenNotFound_throwsNotFoundException() {
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.closeSession(sessionId));
    }

    @Test
    void closeSession_whenAlreadyClosed_throwsForbiddenException() {
        AttendanceSession session = AttendanceSession.create(classId, teacherId);
        session.close();
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(ForbiddenException.class, () -> service.closeSession(sessionId));
    }

    // ── getOpenSession ────────────────────────────────────────────────────────

    @Test
    void getOpenSession_returnsOpenSession() {
        AttendanceSession session = AttendanceSession.create(classId, teacherId);
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.of(session));

        AttendanceSession result = service.getOpenSession(sessionId);

        assertEquals(session, result);
    }

    @Test
    void getOpenSession_whenNotFound_throwsNotFoundException() {
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getOpenSession(sessionId));
    }

    @Test
    void getOpenSession_whenClosed_throwsForbiddenException() {
        AttendanceSession session = AttendanceSession.create(classId, teacherId);
        session.close();
        when(sessionRepository.findActiveById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(ForbiddenException.class, () -> service.getOpenSession(sessionId));
    }
}
