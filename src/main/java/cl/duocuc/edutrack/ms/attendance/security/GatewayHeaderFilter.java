package cl.duocuc.edutrack.ms.attendance.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Provider
public class GatewayHeaderFilter implements ContainerRequestFilter {

    @Inject
    UserContext userContext;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();
        if (path.startsWith("/q/")) {
            return;
        }

        String rawUserId = ctx.getHeaderString("X-User-Id");
        if (rawUserId == null || rawUserId.isBlank()) {
            ctx.abortWith(unauthorized("Missing X-User-Id header"));
            return;
        }

        try {
            userContext.setUserId(UUID.fromString(rawUserId));
        } catch (IllegalArgumentException e) {
            ctx.abortWith(unauthorized("Invalid X-User-Id format"));
            return;
        }

        String rawRoles = ctx.getHeaderString("X-Roles");
        Set<String> roles = rawRoles != null
                ? Arrays.stream(rawRoles.split(",")).map(String::trim).collect(Collectors.toSet())
                : Set.of();
        userContext.setRoles(roles);
        userContext.setCorrelationId(ctx.getHeaderString("X-Correlation-Id"));
    }

    private Response unauthorized(String message) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", Map.of(
                        "code", "UNAUTHORIZED",
                        "message", message,
                        "details", Map.of()
                )))
                .build();
    }
}
