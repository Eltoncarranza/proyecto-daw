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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Usuario doctor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConsulta tipoConsulta;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(precision = 5, scale = 2)
    private Double temperatura;

    @Column(precision = 5, scale = 2)
    private Double peso;

    @Column(precision = 4, scale = 2)
    private Double talla;

    @Column(nullable = false)
    @Builder.Default
    private Boolean finalizada = false;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ArchivoMedico> archivos = new ArrayList<>();

    public enum TipoConsulta { GINECOLOGICA, OBSTETRICA, CONTROL, URGENCIA, SEGUIMIENTO }
}