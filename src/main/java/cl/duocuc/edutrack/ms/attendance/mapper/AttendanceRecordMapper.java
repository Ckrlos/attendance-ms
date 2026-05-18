package cl.duocuc.edutrack.ms.attendance.mapper;

import cl.duocuc.edutrack.ms.attendance.domain.AttendanceRecord;
import cl.duocuc.edutrack.ms.attendance.dto.response.RecordResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AttendanceRecordMapper {

    public RecordResponse toResponse(AttendanceRecord record) {
        RecordResponse r = new RecordResponse();
        r.id            = record.getId();
        r.sessionId     = record.getSession().getId();
        r.studentId     = record.getStudentId();
        r.status        = record.getStatus().name();
        r.captureMethod = record.getCaptureMethod();
        r.metadata      = record.getMetadata();
        r.recordedAt    = record.getRecordedAt();
        r.createdAt     = record.getCreatedAt();
        return r;
    }
}
