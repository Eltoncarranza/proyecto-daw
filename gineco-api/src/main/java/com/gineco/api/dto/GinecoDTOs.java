package com.gineco.api.dto;

import com.gineco.api.entity.ArchivoMedico;
import com.gineco.api.entity.Cita;
import com.gineco.api.entity.Consulta;
import com.gineco.api.entity.Paciente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class GinecoDTOs {

    // ====================== AUTENTICACIÓN (NUEVO) ======================
    // Agregamos esto para solucionar el error "Cannot resolve symbol AuthDTOs"

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El nombre de usuario es requerido")
        private String username;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String username;
        private String nombre;
        private String rol;
    }

    @Data
    public static class CambiarPasswordRequest {
        @NotBlank(message = "La contraseña actual es requerida")
        private String actualPassword;

        @NotBlank(message = "La nueva contraseña es requerida")
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        private String nuevoPassword;
    }

    @Data
    public static class UsuarioDTO {
        private Long id;
        private String username;
        private String nombre;
        private String email;
        private String rol;
        private Boolean activo;
    }

    // ====================== PACIENTE ======================
    // ... (Tu código de PacienteRequest y PacienteResponse está bien)

    @Data
    public static class PacienteRequest {
        @NotBlank(message = "El DNI es requerido")
        @Size(min = 7, max = 20) private String dni;
        @NotBlank private String nombres;
        @NotBlank private String apellidos;
        @NotNull private LocalDate fechaNacimiento;
        private String telefono;
        private String direccion;
        private String email;
        private String grupoSanguineo;
        private String alergias;
        private String antecedentesPersonales;
        private String antecedentesFamiliares;
        private Paciente.TipoPaciente tipoPaciente;
        private String contactoEmergenciaNombre;
        private String contactoEmergenciaTelefono;
        private String contactoEmergenciaRelacion;
    }

    @Data
    public static class PacienteResponse {
        private Long id;
        private String dni;
        private String nombres;
        private String apellidos;
        private String nombreCompleto;
        private LocalDate fechaNacimiento;
        private Integer edad;
        private String telefono;
        private String email;
        private String grupoSanguineo;
        private String alergias;
        private String antecedentesPersonales;
        private String antecedentesFamiliares;
        private Paciente.TipoPaciente tipoPaciente;
        private String contactoEmergenciaNombre;
        private String contactoEmergenciaTelefono;
        private Long totalConsultas;
        private LocalDateTime createdAt;
    }

    // ====================== CONSULTA ======================
    // ... (Tu código de ConsultaRequest y ConsultaResponse está bien)

    @Data
    public static class ConsultaRequest {
        @NotNull private Long pacienteId;
        private Consulta.TipoConsulta tipoConsulta;
        private String motivoConsulta;
        private String anamnesis;
        private String examenFisico;
        private String diagnostico;
        private String tratamiento;
        private String indicaciones;
        private String presionArterial;
        private Double temperatura;
        private Integer frecuenciaCardiaca;
        private Double peso;
        private Double talla;
        private String fcfBebe;
        private String alturaUterina;
        private String hemoglobinaActual;
        private String resultadosLaboratorio;
        private String notasAdicionales;
    }

    @Data
    public static class ConsultaResponse {
        private Long id;
        private Long pacienteId;
        private String pacienteNombre;
        private String doctorNombre;
        private LocalDateTime fechaConsulta;
        private Consulta.TipoConsulta tipoConsulta;
        private String motivoConsulta;
        private String anamnesis;
        private String examenFisico;
        private String diagnostico;
        private String tratamiento;
        private String indicaciones;
        private String presionArterial;
        private Double temperatura;
        private Integer frecuenciaCardiaca;
        private Double peso;
        private String fcfBebe;
        private String alturaUterina;
        private String hemoglobinaActual;
        private String resultadosLaboratorio;
        private String notasAdicionales;
        private Boolean finalizada;
        private List<ArchivoResponse> archivos;
    }

    @Data
    public static class ArchivoResponse {
        private Long id;
        private String nombreOriginal;
        private String urlArchivo;
        private ArchivoMedico.TipoArchivo tipoArchivo;
        private String descripcion;
        private LocalDateTime createdAt;
    }

    // ====================== CITA ======================
    // ... (Tu código de CitaRequest y CitaResponse está bien)

    @Data
    public static class CitaRequest {
        @NotNull private Long pacienteId;
        @NotNull private LocalDate fecha;
        @NotNull private LocalTime horaInicio;
        @NotNull private LocalTime horaFin;
        private String motivo;
        private String notas;
    }

    @Data
    public static class CitaResponse {
        private Long id;
        private Long pacienteId;
        private String pacienteNombre;
        private String pacienteDni;
        private LocalDate fecha;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private Cita.EstadoCita estado;
        private String motivo;
        private String notas;
        private Boolean advertenciaCercana;
    }

    // ====================== GESTANTE ======================
    // ... (Tu código de GestanteRequest y GestanteResponse está bien)

    @Data
    public static class GestanteRequest {
        @NotNull private LocalDate fechaUltimaRegla;
        private LocalDate fechaUltimaEcografia;
        private Integer semanasEcografia;
        private LocalDate fechaProbableParto;
        private Integer gestaciones;
        private Integer partos;
        private Integer cesareas;
        private Integer abortos;
        private String grupoSanguineo;
        private Boolean rhNegativo;
        private Double pesoInicial;
        private Double talla;
        private String factoresRiesgo;
        private Boolean embarazoAltoRiesgo;
        private String hemoglobinaInicial;
        private String hematocritoInicial;
        private Boolean vihResultado;
        private Boolean sifilisCruda;
        private Boolean hepatitisBResultado;
        private String notasGenerales;
    }

    @Data
    public static class GestanteResponse {
        private Long id;
        private Long pacienteId;
        private LocalDate fechaUltimaRegla;
        private LocalDate fechaUltimaEcografia;
        private Integer semanasActuales;
        private LocalDate fechaProbableParto;
        private String recomendacionEcografia;
        private String descripcionEcografia;
        private Integer gestaciones;
        private Integer partos;
        private Integer cesareas;
        private Integer abortos;
        private String grupoSanguineo;
        private Boolean rhNegativo;
        private Double pesoInicial;
        private Double talla;
        private String factoresRiesgo;
        private Boolean embarazoAltoRiesgo;
        private String hemoglobinaInicial;
        private String notasGenerales;
    }
}