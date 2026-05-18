package cl.duocuc.edutrack.ms.attendance.security;

import jakarta.enterprise.context.RequestScoped;
import java.util.Set;
import java.util.UUID;

@RequestScoped
public class UserContext {

    private UUID userId;
    private Set<String> roles;
    private String correlationId;

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public UUID getUserId()         { return userId; }
    public Set<String> getRoles()   { return roles; }
    public String getCorrelationId(){ return correlationId; }

    public void setUserId(UUID userId)           { this.userId = userId; }
    public void setRoles(Set<String> roles)      { this.roles = roles; }
    public void setCorrelationId(String id)      { this.correlationId = id; }
}
