package cl.duocuc.edutrack.ms.attendance;

import cl.duocuc.edutrack.ms.infrastructure.discovery.ServiceIds;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/"+ ServiceIds.ATTENDANCE)
public class AttendanceApplication extends Application {
}
