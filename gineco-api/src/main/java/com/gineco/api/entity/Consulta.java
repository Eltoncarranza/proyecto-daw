package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Consulta extends BaseEntity {

    public enum TipoConsulta {
        PRIMERA_VEZ, CONTROL, EMERGENCIA, REEVALUACION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Usuario doctor;

    @Column(nullable = false)
    private LocalDateTime fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConsulta tipoConsulta;

    @Column(columnDefinition = "TEXT")
    private String motivoConsulta;

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

    @Column(length = 20)
    private String presionArterial;

    // CORRECCIÓN: Sin precision ni scale
    @Column
    private Double temperatura;

    private Integer frecuenciaCardiaca;

    @Column
    private Double peso;

    @Column
    private Double talla;

    @Column(length = 50)
    private String fcfBebe;

    @Column(length = 50)
    private String alturaUterina;

    @Column(length = 20)
    private String hemoglobinaActual;

    @Column(columnDefinition = "TEXT")
    private String resultadosLaboratorio;

    @Column(columnDefinition = "TEXT")
    private String notasAdicionales;

    @Column(nullable = false)
    @Builder.Default
    private Boolean finalizada = false;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArchivoMedico> archivos = new ArrayList<>();
}