package cl.duocuc.edutrack.ms.attendance.resource;

import cl.duocuc.edutrack.ms.attendance.dto.request.CreateRecordRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.RecordResponse;
import cl.duocuc.edutrack.ms.attendance.service.AttendanceRecordService;
import cl.duocuc.edutrack.ms.infrastructure.context.RequestContext;
import cl.duocuc.edutrack.ms.infrastructure.security.Permission;
import cl.duocuc.edutrack.ms.infrastructure.security.RequirePermission;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

import static cl.duocuc.edutrack.ms.attendance.security.AttendencesResourcesId.registros;

@Path("/sessions/{sessionId}/records")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Records", description = "Registro de asistencia por alumno")
public class AttendanceRecordResource {

    @Inject
    AttendanceRecordService recordService;

    @Inject
    RequestContext userContext;

    @POST
    @Operation(summary = "Registrar asistencia", description = "Registra la asistencia de un alumno en una sesión OPEN. Requiere rol TEACHER.")
    @APIResponse(responseCode = "201", description = "Asistencia registrada exitosamente")
    @APIResponse(responseCode = "401", description = "Header X-User-Id ausente")
    @APIResponse(responseCode = "403", description = "La sesión está cerrada o sin permiso")
    @APIResponse(responseCode = "404", description = "Sesión no encontrada")
    @APIResponse(responseCode = "409", description = "Ya existe un registro para este alumno en esta sesión")
    @APIResponse(responseCode = "422", description = "Datos de entrada inválidos")
    @RequirePermission(resource = registros, value = Permission.WRITE)
    public Response registerRecord(@PathParam("sessionId") UUID sessionId,
                                   @Valid CreateRecordRequest request) {
        ApiResponse<RecordResponse> response = recordService.registerRecord(sessionId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
