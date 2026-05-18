package cl.duocuc.edutrack.ms.attendance.resource;

import cl.duocuc.edutrack.ms.attendance.dto.request.CreateSessionRequest;
import cl.duocuc.edutrack.ms.attendance.dto.response.ApiResponse;
import cl.duocuc.edutrack.ms.attendance.dto.response.SessionResponse;
import cl.duocuc.edutrack.ms.attendance.service.AttendanceSessionService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sessions", description = "Gestión de sesiones de asistencia")
public class AttendanceSessionResource {

    @Inject
    AttendanceSessionService sessionService;

    @POST
    @Operation(summary = "Crear sesión de asistencia", description = "Crea una nueva sesión en estado OPEN para una clase")
    @APIResponse(responseCode = "201", description = "Sesión creada exitosamente")
    @APIResponse(responseCode = "422", description = "Datos de entrada inválidos")
    public Response createSession(@Valid CreateSessionRequest request) {
        ApiResponse<SessionResponse> response = sessionService.createSession(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}/close")
    @Operation(summary = "Cerrar sesión de asistencia", description = "Transiciona la sesión de OPEN a CLOSED. Operación irreversible.")
    @APIResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    @APIResponse(responseCode = "403", description = "La sesión ya está cerrada")
    @APIResponse(responseCode = "404", description = "Sesión no encontrada")
    public Response closeSession(@PathParam("id") UUID sessionId) {
        ApiResponse<SessionResponse> response = sessionService.closeSession(sessionId);
        return Response.ok(response).build();
    }
}
