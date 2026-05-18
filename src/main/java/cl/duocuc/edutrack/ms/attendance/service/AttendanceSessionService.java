package cl.duocuc.edutrack.ms.attendance.service;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.dto.request.CreateSessionRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import cl.duocuc.edutrack.ms.attendance.exception.SessionClosedException;
import cl.duocuc.edutrack.ms.attendance.exception.SessionNotFoundException;
import cl.duocuc.edutrack.ms.attendance.mapper.AttendanceSessionMapper;
import cl.duocuc.edutrack.ms.attendance.repository.AttendanceSessionRepository;
import cl.duocuc.edutrack.ms.attendance.security.UserContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class AttendanceSessionService {

    @Inject
    AttendanceSessionRepository sessionRepository;

    @Inject
    AttendanceSessionMapper sessionMapper;

    @Inject
    UserContext userContext;

    @Transactional
    public ApiResponse<SessionResponse> createSession(CreateSessionRequest request) {
        AttendanceSession session = AttendanceSession.create(request.classId, userContext.getUserId());
        sessionRepository.persist(session);
        return new ApiResponse<>(sessionMapper.toResponse(session));
    }

    @Transactional
    public ApiResponse<SessionResponse> closeSession(UUID sessionId) {
        AttendanceSession session = sessionRepository.findActiveById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        try {
            session.close();
        } catch (IllegalStateException e) {
            throw new SessionClosedException(sessionId);
        }

        return new ApiResponse<>(sessionMapper.toResponse(session));
    }

    public AttendanceSession getOpenSession(UUID sessionId) {
        AttendanceSession session = sessionRepository.findActiveById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        try {
            session.validateOpen();
        } catch (IllegalStateException e) {
            throw new SessionClosedException(sessionId);
        }

        return session;
    }
}
