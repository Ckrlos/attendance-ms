package cl.duocuc.edutrack.ms.attendance.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RecordResponse {

    public UUID id;
    public UUID sessionId;
    public UUID studentId;
    public String status;
    public String captureMethod;
    public Map<String, Object> metadata;
    public LocalDateTime recordedAt;
    public LocalDateTime createdAt;
}
