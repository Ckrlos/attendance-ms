package cl.duocuc.edutrack.ms.attendance.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionResponse {

    public UUID id;
    public UUID classId;
    public UUID teacherId;
    public String status;
    public LocalDateTime sessionDate;
    public LocalDateTime closedAt;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
