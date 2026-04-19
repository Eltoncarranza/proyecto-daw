package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "pacientes_gestantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteGestante extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    // Datos del embarazo
    @Column(nullable = false)
    private LocalDate fechaUltimaRegla;

    private LocalDate fechaUltimaEcografia;
    private Integer semanasEcografia; // semanas al momento de la eco

    // Fecha probable de parto (calculada y ajustable)
    private LocalDate fechaProbableParto;

    // Datos médicos obstetrícos
    private Integer gestaciones;     // número de gestaciones previas
    private Integer partos;          // partos anteriores
    private Integer cesareas;        // cesáreas anteriores
    private Integer abortos;         // abortos previos
    private Integer hijos_vivos;

    @Column(length = 5)
    private String grupoSanguineo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean rhNegativo = false;

    // Peso y talla al inicio
    private Double pesoInicial;
    private Double talla;

    // Datos de riesgo
    @Column(length = 1000)
    private String factoresRiesgo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean embarazoAltoRiesgo = false;

    // Resultados de laboratorio iniciales
    @Column(length = 10)
    private String hemoglobinaInicial;
    @Column(length = 10)
    private String hematocritoInicial;
    private Boolean vihResultado;
    private Boolean sifilisCruda;
    private Boolean hepatitisBResultado;
    private Boolean toxoplasmaIgg;

    // Notas generales del embarazo
    @Column(columnDefinition = "TEXT")
    private String notasGenerales;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Calcula las semanas de gestación actuales basadas en FUR.
     * Si hay fecha de eco, ajusta desde esa referencia.
     */
    public int calcularSemanasActuales() {
        LocalDate referencia = fechaUltimaRegla;
        if (fechaUltimaEcografia != null && semanasEcografia != null) {
            long diasDesdeEco = ChronoUnit.DAYS.between(fechaUltimaEcografia, LocalDate.now());
            return (int) (semanasEcografia + (diasDesdeEco / 7));
        }
        long dias = ChronoUnit.DAYS.between(referencia, LocalDate.now());
        return (int) (dias / 7);
    }

    /**
     * Calcula la fecha probable de parto usando Regla de Naegele.
     */
    public LocalDate calcularFPP() {
        if (fechaProbableParto != null) return fechaProbableParto;
        return fechaUltimaRegla.plusDays(280);
    }

    /**
     * Recomienda el tipo de ecografía según semanas actuales.
     */
    public String recomendarEcografia() {
        int semanas = calcularSemanasActuales();
        if (semanas < 11) {
            return "ECO_TEMPRANA";
        } else if (semanas >= 11 && semanas <= 14) {
            return "ECO_PRIMER_TRIMESTRE";
        } else if (semanas >= 18 && semanas <= 24) {
            return "ECO_MORFOLOGICA";
        } else if (semanas >= 28 && semanas <= 32) {
            return "ECO_TERCER_TRIMESTRE";
        } else if (semanas >= 36) {
            return "ECO_FINAL";
        }
        return "NINGUNA_URGENTE";
    }
}
