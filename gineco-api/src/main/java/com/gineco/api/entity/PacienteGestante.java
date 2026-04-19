package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "pacientes_gestantes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PacienteGestante extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDate fechaUltimaRegla;

    private LocalDate fechaUltimaEcografia;
    private Integer semanasEcografia;
    private LocalDate fechaProbableParto;

    private Integer gestaciones;
    private Integer partos;
    private Integer cesareas;
    private Integer abortos;
    private Integer hijos_vivos;

    @Column(length = 5)
    private String grupoSanguineo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean rhNegativo = false;

    // CORRECCIÓN: Los tipos Double ya no tienen precision ni scale
    @Column
    private Double pesoInicial;

    @Column
    private Double talla;

    @Column(length = 1000)
    private String factoresRiesgo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean embarazoAltoRiesgo = false;

    @Column(length = 10)
    private String hemoglobinaInicial;
    @Column(length = 10)
    private String hematocritoInicial;
    private Boolean vihResultado;
    private Boolean sifilisCruda;
    private Boolean hepatitisBResultado;
    private Boolean toxoplasmaIgg;

    @Column(columnDefinition = "TEXT")
    private String notasGenerales;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    // Métodos para cálculos y MapStruct
    public Integer getSemanasActuales() {
        if (fechaUltimaEcografia != null && semanasEcografia != null) {
            long diasDesdeEco = ChronoUnit.DAYS.between(fechaUltimaEcografia, LocalDate.now());
            return (int) (semanasEcografia + (diasDesdeEco / 7));
        }
        if (fechaUltimaRegla == null) return 0;
        long dias = ChronoUnit.DAYS.between(fechaUltimaRegla, LocalDate.now());
        return (int) (dias / 7);
    }

    public LocalDate getFechaProbablePartoCalculada() {
        if (fechaProbableParto != null) return fechaProbableParto;
        if (fechaUltimaRegla == null) return null;
        return fechaUltimaRegla.plusDays(280);
    }

    public String getRecomendacionEcografia() {
        int semanas = getSemanasActuales();
        if (semanas < 11) return "ECO_TEMPRANA";
        if (semanas <= 14) return "ECO_PRIMER_TRIMESTRE";
        if (semanas <= 24) return "ECO_MORFOLOGICA";
        if (semanas <= 32) return "ECO_TERCER_TRIMESTRE";
        if (semanas >= 36) return "ECO_FINAL";
        return "NINGUNA_URGENTE";
    }
}