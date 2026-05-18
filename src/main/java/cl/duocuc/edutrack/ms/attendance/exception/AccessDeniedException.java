package cl.duocuc.edutrack.ms.attendance.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String requiredRole) {
        super("Access denied. Required role: " + requiredRole);
    }
}
