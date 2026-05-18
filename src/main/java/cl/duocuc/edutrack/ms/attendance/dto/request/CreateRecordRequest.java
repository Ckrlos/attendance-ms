package cl.duocuc.edutrack.ms.attendance.dto.request;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CreateRecordRequest {

    @NotNull
    @Schema(description = "UUID externo del alumno (student-ms)", required = true)
    public UUID studentId;

    @NotNull
    @Schema(description = "Estado de asistencia: PRESENT, ABSENT o JUSTIFIED", required = true)
    public String status;

    @Schema(description = "Método de captura (MANUAL, BIOMETRIC, RFID, etc.)")
    public String captureMethod;

    @Schema(description = "Mapa clave-valor libre, se almacena sin interpretar")
    public Map<String, Object> metadata;

    @NotNull
    @Schema(description = "Timestamp de la captura en formato ISO 8601", required = true)
    public LocalDateTime recordedAt;
}
