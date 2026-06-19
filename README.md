# ms-attendance

Microservicio de registro y control de asistencia del sistema **EduTrack** (Duoc UC — DSY1106).  
Responsable de crear sesiones de clase, registrar la asistencia de alumnos y exponer los recursos
que el módulo de permisos puede asignar.

---

## Stack tecnológico

| Capa            | Tecnología                                      |
|-----------------|-------------------------------------------------|
| Runtime         | Java 21 + Quarkus 3.35.3                        |
| API             | RESTEasy Reactive (JAX-RS)                      |
| Persistencia    | Hibernate ORM Panache — patrón Repository       |
| Base de datos   | PostgreSQL 15+                                  |
| Migraciones     | Flyway (corre automáticamente al arrancar)      |
| Serialización   | Jackson                                         |
| Validación      | Hibernate Validator (Jakarta Bean Validation)   |
| Documentación   | SmallRye OpenAPI + Swagger UI                   |
| Seguridad       | `edutrack-ms-commons` — headers internos del Gateway |
| Tests           | JUnit 5 + Mockito                               |
| Build           | Maven Wrapper (`./mvnw`)                        |

---

## Prerrequisitos

- Java 21
- Maven (o usar `./mvnw` incluido)
- PostgreSQL corriendo localmente (o en Docker)
- `edutrack-ms-commons:1.0.0` instalado en el repositorio Maven local  
  → se instala desde el módulo `commons/` del monorepo: `./mvnw install`

---

## Configuración local

Crea un archivo `.env` en la raíz del módulo (no se sube al repositorio):

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=edutrack
DB_USER=attendance_user
DB_PASSWORD=tu_password
```

Las variables tienen valores por defecto definidos en `application.properties`, pero sin
el `.env` correctamente configurado el servicio no levantará contra una BD real.

### Base de datos

Solo necesitas que existan el usuario y la base de datos. El schema `attendance` y todas
las tablas las crea **Flyway automáticamente** al arrancar el servicio por primera vez:

```sql
-- Ejemplo mínimo en psql
CREATE DATABASE edutrack;
CREATE USER attendance_user WITH PASSWORD 'tu_password';
GRANT ALL PRIVILEGES ON DATABASE edutrack TO attendance_user;
```

---

## Levantar el servicio

```bash
./mvnw quarkus:dev
```

El servicio queda disponible en `http://localhost:8080/attendance`.

Swagger UI: `http://localhost:8080/attendance/q/swagger-ui`  
Health check: `http://localhost:8080/attendance/q/health`

En modo `dev`, Quarkus recarga automáticamente con cada cambio de código sin reiniciar.

---

## Endpoints

Base URL: `/attendance`

### Sesiones

| Método  | Ruta                      | Descripción                                  | Permiso requerido        |
|---------|---------------------------|----------------------------------------------|--------------------------|
| `POST`  | `/sessions`               | Crea una sesión de clase en estado `OPEN`    | `attendance_sessions:WRITE` |
| `PATCH` | `/sessions/{id}/close`    | Cierra la sesión (`OPEN → CLOSED`). Irreversible | `attendance_sessions:WRITE` |

### Registros de asistencia

| Método  | Ruta                              | Descripción                              | Permiso requerido         |
|---------|-----------------------------------|------------------------------------------|---------------------------|
| `POST`  | `/sessions/{sessionId}/records`   | Registra la asistencia de un alumno      | `attendance_records:WRITE` |

### Meta (sin autenticación)

| Método | Ruta              | Descripción                                           |
|--------|-------------------|-------------------------------------------------------|
| `GET`  | `/meta/resources` | Lista los resource keys que este servicio protege     |

---

## Seguridad

Este microservicio **no valida JWT**. La autenticación la realiza el API Gateway, que luego
inyecta headers internos en cada request:

| Header            | Contenido                                   |
|-------------------|---------------------------------------------|
| `X-User-Id`       | UUID del usuario autenticado                |
| `X-Roles`         | IDs de roles del usuario (lista de UUIDs)   |
| `X-Correlation-Id`| ID de trazabilidad entre servicios          |

La autorización usa el sistema de permisos Unix-style de `edutrack-ms-commons`
(`@RequirePermission`). Si el Gateway no incluye los headers esperados, el request es rechazado
con `401`.

---

## Esquema de base de datos

Schema: `attendance`

```
attendance_sessions
├── id           UUID  PK
├── class_id     UUID  (referencia externa a course-ms)
├── teacher_id   UUID  (referencia externa a user-ms)
├── status       VARCHAR(10)  CHECK (OPEN | CLOSED)
├── session_date TIMESTAMP
├── closed_at    TIMESTAMP  nullable
├── created_at   TIMESTAMP
├── updated_at   TIMESTAMP
└── deleted_at   TIMESTAMP  nullable  ← soft delete

attendance_records
├── id             UUID  PK
├── session_id     UUID  FK → attendance_sessions
├── student_id     UUID  (referencia externa a student-ms)
├── status         VARCHAR(10)  CHECK (PRESENT | ABSENT | JUSTIFIED)
├── capture_method VARCHAR(50)  nullable  (MANUAL | BIOMETRIC | RFID | etc.)
├── metadata       JSONB  nullable  ← extensible sin cambiar schema
├── recorded_at    TIMESTAMP
├── created_at     TIMESTAMP
├── updated_at     TIMESTAMP
└── deleted_at     TIMESTAMP  nullable  ← soft delete
```

Restricción importante: `UNIQUE (student_id, session_id)` — un alumno no puede tener más de
un registro por sesión (retorna `409` si se intenta duplicar).

---

## Estructura del proyecto

```
src/
├── main/java/.../attendance/
│   ├── domain/           ← entidades JPA con factory estático (sin setters públicos)
│   ├── dto/
│   │   ├── request/      ← CreateSessionRequest, CreateRecordRequest
│   │   └── response/     ← SessionResponse, RecordResponse, ApiResponse<T>
│   ├── mapper/           ← conversión entidad → DTO
│   ├── repository/       ← Panache repositories (patrón Repository, no Active Record)
│   ├── resource/         ← endpoints JAX-RS
│   ├── security/         ← AttendencesResourcesId (catálogo de resource keys)
│   └── service/          ← lógica de negocio
└── main/resources/
    ├── application.properties
    └── db/migration/     ← scripts Flyway (V<timestamp>__descripcion.sql)
```

---

## Pruebas

```bash
# Ejecutar todos los tests
./mvnw test

# Ver reporte de cobertura (JaCoCo)
./mvnw test && open target/jacoco-report/index.html
```

Las pruebas son **unitarias puras** — no necesitan base de datos ni levantar Quarkus.
Usan Mockito para simular repositorios y dependencias. El suite completo corre en ~2 segundos.

Ver `src/test/java/.../attendance/TESTS.md` para la descripción detallada de cada prueba.

---

## Colección Bruno

En `bruno/attendance-ms/` hay una colección lista para importar en [Bruno](https://www.usebruno.com/).
Cubre todos los endpoints con datos de ejemplo, incluyendo casos de error (401, 403, 404, 409).

Pasos:
1. Abrir Bruno → **Open Collection** → seleccionar `bruno/attendance-ms/`
2. Seleccionar el environment **local**
3. Ejecutar `sessions/01-crear-sesion` primero — guarda el `sessionId` automáticamente para los requests siguientes

---

## Convenciones del equipo

- Los Is de recursos externos (`class_id`, `student_id`, `teacher_id`) son UUIDs opacos — este
  servicio no hace joins cross-schema ni consultas a otros microservicios.
- **Nunca se hace `DELETE` físico** — siempre soft delete via `deleted_at`.
- El campo `metadata` en los registros es JSONB libre: se almacena sin interpretar, pensado para
  extensibilidad futura (biometría, RFID, etc.) sin cambiar el schema.
- Las migraciones Flyway usan el formato `V<YYYYMMDDHHMMSS>__descripcion.sql`. No modificar
  migraciones ya aplicadas; siempre agregar una nueva.
- `groupId`: `cl.duocuc.edutrack` / `artifactId`: `ms-attendance`
