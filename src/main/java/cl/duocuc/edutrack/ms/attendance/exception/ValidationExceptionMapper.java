package cl.duocuc.edutrack.ms.attendance.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        return Response.status(422)
                .entity(Map.of("error", Map.of(
                        "code", "VALIDATION_ERROR",
                        "message", "Validation failed",
                        "details", Map.of("violations", details)
                )))
                .build();
    }
}
