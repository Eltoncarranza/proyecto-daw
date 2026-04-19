# GinecoSys API — Gineco-Obstetricia

API REST desarrollada en **Java 17 + Spring Boot 3.2** con PostgreSQL (Supabase).

---

## Requisitos

- Java 17+
- Maven 3.8+
- Conexión a internet (Supabase PostgreSQL)

---

## Configuración

Las credenciales de Supabase ya están en `src/main/resources/application.properties`.

---

## Ejecutar el proyecto

```bash
# Desde la carpeta gineco-api/
mvn spring-boot:run
```

La API se levanta en: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Usuario por defecto

Al iniciar por primera vez se crea automáticamente:

| Campo    | Valor        |
|----------|-------------|
| Usuario  | `doctor`    |
| Password | `gineco2025`|

---

## Endpoints principales

### Autenticación
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/cambiar-password` | Cambiar contraseña |

### Pacientes
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/pacientes?busqueda=...` | Listar / buscar por DNI o nombre |
| GET | `/api/pacientes/{id}` | Obtener paciente |
| GET | `/api/pacientes/dni/{dni}` | Buscar por DNI |
| POST | `/api/pacientes` | Registrar nueva paciente |
| PUT | `/api/pacientes/{id}` | Actualizar datos |
| PATCH | `/api/pacientes/{id}/tipo` | Cambiar tipo (ginecológica/gestante) |
| DELETE | `/api/pacientes/{id}` | Dar de baja (soft delete) |

### Consultas
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/consultas/hoy` | Consultas del día actual |
| GET | `/api/consultas/paciente/{id}/historial` | Historial completo |
| POST | `/api/consultas` | Nueva consulta |
| PUT | `/api/consultas/{id}` | Editar consulta (fecha NO cambia) |
| PATCH | `/api/consultas/{id}/finalizar` | Finalizar consulta |
| POST | `/api/consultas/{id}/archivos` | Subir ecografía/PDF |
| DELETE | `/api/consultas/archivos/{id}` | Eliminar archivo |

### Embarazo (Gestante)
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/pacientes/{id}/gestante` | Ver datos del embarazo + recomendación eco |
| POST | `/api/pacientes/{id}/gestante` | Registrar datos del embarazo |
| PUT | `/api/pacientes/{id}/gestante` | Actualizar datos del embarazo |

### Citas / Agenda
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/citas/agenda?fecha=2025-06-15` | Agenda del día |
| GET | `/api/citas/paciente/{id}` | Citas de una paciente |
| POST | `/api/citas` | Registrar cita (valida conflictos) |
| PUT | `/api/citas/{id}` | Modificar cita |
| PATCH | `/api/citas/{id}/estado` | Cambiar estado |
| DELETE | `/api/citas/{id}` | Cancelar cita |

---

## Seguridad

Todas las rutas excepto `/api/auth/login` requieren header:
```
Authorization: Bearer <token>
```

El token se obtiene en el login y expira en 30 minutos.

---

## Lógica de ecografías (Gestante)

El sistema calcula automáticamente las semanas de gestación y recomienda:

| Semanas | Recomendación |
|---------|---------------|
| < 11    | Ecografía de datación |
| 11–14   | Tamizaje genético (translucencia nucal) |
| 18–24   | Morfológica estructural |
| 28–32   | Tercer trimestre |
| ≥ 36    | Ecografía de término |

---

## Base de datos

Las tablas se crean automáticamente al iniciar (`ddl-auto=update`):

- `usuarios` — Doctores y personal
- `pacientes` — Todas las pacientes
- `pacientes_gestantes` — Datos obstétricos del embarazo
- `consultas` — Historial clínico
- `archivos_medicos` — Ecografías y PDFs (almacenados en Supabase Storage)
- `citas` — Agenda médica

