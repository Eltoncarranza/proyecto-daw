package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Usuario doctor;

    // Fecha NO modificable después de crear
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConsulta tipoConsulta;

    // Motivo de consulta
    @Column(length = 500)
    private String motivoConsulta;

    // Historia clínica - campos editables
    @Column(columnDefinition = "TEXT")
    private String anamnesis;

    @Column(columnDefinition = "TEXT")
    private String examenFisico;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    // Signos vitales
    @Column(length = 20)
    private String presionArterial;
    private Double temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Double peso;
    private Double talla;

    // Datos extra para gestantes
    @Column(length = 20)
    private String fcfBebe; // frecuencia cardíaca fetal
    @Column(length = 20)
    private String alturaUterina;
    @Column(length = 10)
    private String hemoglobinaActual;

    // Resultados de laboratorio
    @Column(columnDefinition = "TEXT")
    private String resultadosLaboratorio;

    @Column(columnDefinition = "TEXT")
    private String notasAdicionales;

    @Column(nullable = false)
    @Builder.Default
    private Boolean finalizada = false;

    // Archivos adjuntos (ecografías, PDFs)
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ArchivoMedico> archivos = new ArrayList<>();

    public enum TipoConsulta {
        GINECOLOGICA, OBSTETRICA, CONTROL, URGENCIA, SEGUIMIENTO
    }
}
