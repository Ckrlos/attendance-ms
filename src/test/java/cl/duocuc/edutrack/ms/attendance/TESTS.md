# Pruebas unitarias — attendance-ms

## ¿Tocan la base de datos?

**No.** Son pruebas unitarias puras. No levantan Quarkus, no abren conexiones, no ejecutan
Flyway. Cada dependencia que normalmente llegaría a la BD (repositorios) es reemplazada por un
objeto falso (mock) controlado por Mockito. El código bajo prueba nunca sabe si hay una BD real
o no.

---

## Estándar que siguen

| Decisión              | Detalle                                                                 |
|-----------------------|-------------------------------------------------------------------------|
| Framework de pruebas  | JUnit 5 (Jupiter)                                                       |
| Mocks                 | Mockito vía `@ExtendWith(MockitoExtension.class)` + `@InjectMocks`      |
| Convención de nombres | `método_escenario_resultadoEsperado` en camelCase                       |
| Afirmaciones          | `assertEquals`, `assertThrows`, `assertNull`, etc. de JUnit 5 nativo   |
| Sin Quarkus           | Ningún test usa `@QuarkusTest`; arrancan en ~10 ms sin contenedor       |
| Sin BD                | Repositorios mockeados con `@Mock`; nunca se llama a la BD real         |

---

## Cómo se estructuraron las pruebas

```
src/test/java/.../attendance/
├── domain/
│   ├── AttendanceSessionTest.java    ← pruebas del modelo de sesión
│   └── AttendanceRecordTest.java     ← pruebas del modelo de registro
├── mapper/
│   ├── AttendanceSessionMapperTest.java
│   └── AttendanceRecordMapperTest.java
└── service/
    ├── AttendanceSessionServiceTest.java
    └── AttendanceRecordServiceTest.java
```

Cada capa tiene sus propias pruebas aisladas. Las capas superiores (service) reciben mocks de
las inferiores (repository), así que si un test de servicio falla, el problema está en la lógica
del servicio, no en la BD ni en el mapper.

---

## Qué prueba cada archivo

### `domain/AttendanceSessionTest` — 7 pruebas

Prueba la entidad `AttendanceSession` sin ninguna dependencia externa.

| Prueba                                             | Qué verifica                                                   |
|----------------------------------------------------|----------------------------------------------------------------|
| `create_setsAllFieldsCorrectly`                    | El factory asigna classId, teacherId, status=OPEN y fechas     |
| `create_generatesUniqueIds`                        | Dos llamadas a `create()` generan UUIDs distintos              |
| `close_transitionsStatusToClosed`                  | `close()` cambia el status de OPEN a CLOSED                    |
| `close_setsClosedAt`                               | `close()` asigna la fecha de cierre                            |
| `close_whenAlreadyClosed_throwsIllegalStateException` | Cerrar una sesión ya cerrada lanza excepción               |
| `validateOpen_whenOpen_doesNotThrow`               | Una sesión OPEN pasa la validación sin error                   |
| `validateOpen_whenClosed_throwsIllegalStateException` | Una sesión CLOSED no pasa la validación                    |

### `domain/AttendanceRecordTest` — 7 pruebas

Prueba la entidad `AttendanceRecord` y sus validaciones en el factory estático.

| Prueba                                           | Qué verifica                                               |
|--------------------------------------------------|------------------------------------------------------------|
| `create_withAllFields_setsCorrectly`             | El factory asigna todos los campos correctamente           |
| `create_withOptionalFieldsNull_succeeds`         | `captureMethod` y `metadata` pueden ser null               |
| `create_generatesUniqueIds`                      | Cada registro recibe un UUID único                         |
| `create_withNullSession_throws`                  | Sin sesión lanza `IllegalArgumentException("session")`     |
| `create_withNullStudentId_throws`                | Sin studentId lanza `IllegalArgumentException("studentId")`|
| `create_withNullStatus_throws`                   | Sin status lanza `IllegalArgumentException("status")`      |
| `create_withNullRecordedAt_throws`               | Sin recordedAt lanza `IllegalArgumentException("recordedAt")`|

### `mapper/AttendanceSessionMapperTest` — 2 pruebas

Prueba la conversión de `AttendanceSession` a `SessionResponse`.

| Prueba                                   | Qué verifica                                        |
|------------------------------------------|-----------------------------------------------------|
| `toResponse_mapsOpenSessionCorrectly`    | Todos los campos se mapean; `closedAt` es null      |
| `toResponse_whenClosed_mapsClosedStatus` | Status es "CLOSED" y `closedAt` tiene valor         |

### `mapper/AttendanceRecordMapperTest` — 3 pruebas

Prueba la conversión de `AttendanceRecord` a `RecordResponse`.

| Prueba                                    | Qué verifica                                        |
|-------------------------------------------|-----------------------------------------------------|
| `toResponse_mapsAllFieldsCorrectly`       | Todos los campos incluyendo sessionId y metadata    |
| `toResponse_withNullOptionals_setsNulls`  | captureMethod y metadata nulos se propagan como null|
| `toResponse_mapsAbsentAndJustifiedStatuses` | Los tres estados (ABSENT, JUSTIFIED) se mapean    |

### `service/AttendanceSessionServiceTest` — 10 pruebas

Prueba `AttendanceSessionService` con repositorio, mapper y `RequestContext` mockeados.

**createSession:**

| Prueba                                  | Qué verifica                                              |
|-----------------------------------------|-----------------------------------------------------------|
| `createSession_persistsSession`         | Llama a `repository.persist()` con una sesión             |
| `createSession_usesTeacherIdFromContext`| El teacherId viene del header `X-User-Id`, no del body    |
| `createSession_returnsWrappedResponse`  | Retorna `ApiResponse<SessionResponse>` con el data correcto|

**closeSession:**

| Prueba                                       | Qué verifica                                        |
|----------------------------------------------|-----------------------------------------------------|
| `closeSession_closesOpenSession`             | La sesión queda en estado CLOSED                    |
| `closeSession_returnsWrappedResponse`        | Retorna `ApiResponse` con el data correcto          |
| `closeSession_whenNotFound_throwsNotFoundException` | Sesión inexistente → 404                    |
| `closeSession_whenAlreadyClosed_throwsForbiddenException` | Sesión cerrada → 403              |

**getOpenSession:**

| Prueba                                            | Qué verifica                        |
|---------------------------------------------------|-------------------------------------|
| `getOpenSession_returnsOpenSession`               | Devuelve la sesión si está OPEN     |
| `getOpenSession_whenNotFound_throwsNotFoundException` | Sesión inexistente → 404        |
| `getOpenSession_whenClosed_throwsForbiddenException` | Sesión cerrada → 403            |

### `service/AttendanceRecordServiceTest` — 9 pruebas

Prueba `AttendanceRecordService` con repositorio, sessionService y mapper mockeados.

| Prueba                                          | Qué verifica                                               |
|-------------------------------------------------|------------------------------------------------------------|
| `registerRecord_persistsRecord`                 | Llama a `repository.persist()` con el registro creado      |
| `registerRecord_returnsWrappedResponse`         | Retorna `ApiResponse<RecordResponse>` con el data correcto |
| `registerRecord_persistsCorrectStudentAndStatus`| El registro guardado tiene el studentId y status correctos |
| `registerRecord_withMetadataAndCaptureMethod`   | captureMethod y metadata se persisten sin modificar        |
| `registerRecord_whenDuplicate_throwsConflictException` | Alumno repetido en misma sesión → 409            |
| `registerRecord_withInvalidStatus_throwsIllegalArgumentException` | Status inválido → error con mensaje    |
| `registerRecord_allValidStatuses_doNotThrow`    | PRESENT, ABSENT y JUSTIFIED son aceptados                  |
| `registerRecord_whenSessionNotFound_throwsNotFoundException` | Error de sesión se propaga            |
| `registerRecord_whenSessionClosed_throwsForbiddenException` | Error de sesión cerrada se propaga    |

---

## Qué NO cubren estas pruebas

- **Endpoints HTTP** — no se prueba que el recurso JAX-RS devuelva 201 o 403
- **Filtro de seguridad** (`RequirePermissionFilter`) — es de la librería commons, se asume probado ahí
- **Persistencia real** — no se verifica que las queries JPQL sean correctas
- **Migración Flyway** — no se verifica que el schema SQL sea compatible con las entidades

Para cubrir esos puntos se necesitarían pruebas de integración con `@QuarkusTest` y
una base de datos de prueba (Testcontainers + PostgreSQL).
