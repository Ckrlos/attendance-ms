package cl.duocuc.edutrack.ms.attendance.resource;

import cl.duocuc.edutrack.ms.attendance.security.AttendencesResourcesId;
import cl.duocuc.edutrack.ms.infrastructure.discovery.ServiceIds;
import cl.duocuc.edutrack.ms.infrastructure.rest.DataResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * Metadatos de auto-descripción del Attendance Service (contrato transversal
 * {@code GET /<servicio>/meta/...}, ver {@code infrastructure.rest}). Expone el
 * catálogo de <em>resource keys</em> que este servicio protege con permisos
 * Unix-style — fuente de verdad descentralizada derivada en código de
 * {@link AttendencesResourcesId}. Público tras el Gateway (sin {@code @RequirePermission}).
 */
@Path("/meta/resources")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Meta", description = "Auto-descripción del servicio (resource keys asignables)")
public class ResourceCatalogResource {

    private static final List<String> RESOURCE_KEYS = List.of(
        AttendencesResourcesId.sesiones,
        AttendencesResourcesId.registros
    );

    @GET
    @Operation(summary = "Catálogo de resource keys que este servicio protege con permisos")
    public DataResponse<List<String>> resources() {
        return DataResponse.of(RESOURCE_KEYS)
            .with("service", ServiceIds.ATTENDANCE)
            .with("count", RESOURCE_KEYS.size());
    }
}
