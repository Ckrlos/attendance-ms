package cl.duocuc.edutrack.ms.attendance.dto.request;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

public class CreateSessionRequest {

    @NotNull
    @Schema(description = "UUID externo de la clase (course-ms)", required = true)
    public UUID classId;

}
