package cl.duocuc.edutrack.ms.attendance.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        if (ex instanceof WebApplicationException wae) {
            return wae.getResponse();
        }
        if (ex instanceof AccessDeniedException) {
            return error(Response.Status.FORBIDDEN, "FORBIDDEN", ex.getMessage());
        }
        if (ex instanceof SessionNotFoundException) {
            return error(Response.Status.NOT_FOUND, "SESSION_NOT_FOUND", ex.getMessage());
        }
        if (ex instanceof SessionClosedException) {
            return error(Response.Status.FORBIDDEN, "SESSION_CLOSED", ex.getMessage());
        }
        if (ex instanceof DuplicateAttendanceException) {
            return error(Response.Status.CONFLICT, "ATTENDANCE_ALREADY_EXISTS", ex.getMessage());
        }
        if (ex instanceof IllegalArgumentException) {
            return error(422, "INVALID_INPUT", ex.getMessage());
        }
        return error(500, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private Response error(Response.Status status, String code, String message) {
        return error(status.getStatusCode(), code, message);
    }

    private Response error(int status, String code, String message) {
        return Response.status(status)
                .entity(Map.of("error", Map.of(
                        "code", code,
                        "message", message,
                        "details", Map.of()
                )))
                .build();
    }
}
