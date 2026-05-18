package cl.duocuc.edutrack.ms.attendance.exception;

import java.util.UUID;

public class SessionClosedException extends RuntimeException {

    public SessionClosedException(UUID sessionId) {
        super("Session is CLOSED and cannot be modified: " + sessionId);
    }
}
