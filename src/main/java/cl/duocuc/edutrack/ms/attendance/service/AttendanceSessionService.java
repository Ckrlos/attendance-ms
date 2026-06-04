package cl.duocuc.edutrack.ms.attendance.service;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceSession;
import cl.duocuc.edutrack.ms.attendance.dto.request.CreateSessionRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import cl.duocuc.edutrack.ms.attendance.mapper.AttendanceSessionMapper;
import cl.duocuc.edutrack.ms.attendance.repository.AttendanceSessionRepository;
import cl.duocuc.edutrack.ms.infrastructure.context.RequestContext;
import cl.duocuc.edutrack.ms.infrastructure.exception.ForbiddenException;
import cl.duocuc.edutrack.ms.infrastructure.exception.NotFoundException;
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
    RequestContext userContext;

    @Transactional
    public ApiResponse<SessionResponse> createSession(CreateSessionRequest request) {
        AttendanceSession session = AttendanceSession.create(request.classId, userContext.headers().requireUserId());
        sessionRepository.persist(session);
        return new ApiResponse<>(sessionMapper.toResponse(session));
    }

    @Transactional
    public ApiResponse<SessionResponse> closeSession(UUID sessionId) {
        AttendanceSession session = sessionRepository.findActiveById(sessionId)
                .orElseThrow(() -> new NotFoundException("","Session not found"));

        try {
            session.close();
        } catch (IllegalStateException e) {
            throw new ForbiddenException("", "Session is CLOSED and cannot be modified" + sessionId);
        }

        return new ApiResponse<>(sessionMapper.toResponse(session));
    }

    public AttendanceSession getOpenSession(UUID sessionId) {
        AttendanceSession session = sessionRepository.findActiveById(sessionId)
                .orElseThrow(() -> new NotFoundException("","Session not found"));

        try {
            session.validateOpen();
        } catch (IllegalStateException e) {
            throw new ForbiddenException("", "Session is CLOSED and cannot be modified" + sessionId);
        }

        return session;
    }
}
