package cl.duocuc.edutrack.ms.attendance.exception;

import java.util.UUID;

public class DuplicateAttendanceException extends RuntimeException {

    public DuplicateAttendanceException(UUID studentId, UUID sessionId) {
        super("Attendance record already exists for student " + studentId + " in session " + sessionId);
    }
}
