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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class AttendanceRecordService {

    @Inject
    AttendanceRecordRepository recordRepository;

    @Inject
    AttendanceSessionService sessionService;

    @Inject
    AttendanceRecordMapper recordMapper;

    @Transactional
    public ApiResponse<RecordResponse> registerRecord(UUID sessionId, CreateRecordRequest request) {
        AttendanceSession session = sessionService.getOpenSession(sessionId);

        if (recordRepository.existsByStudentAndSession(request.studentId, sessionId)) {
            throw new ConflictException("","Ya existe un registro para este alumno en esta sesión");
        }

        AttendanceStatus status;
        try {
            status = AttendanceStatus.valueOf(request.status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.status + ". Must be PRESENT, ABSENT or JUSTIFIED");
        }

        AttendanceRecord record = AttendanceRecord.create(
                session,
                request.studentId,
                status,
                request.captureMethod,
                request.metadata,
                request.recordedAt
        );

        recordRepository.persist(record);
        return new ApiResponse<>(recordMapper.toResponse(record));
    }
}
