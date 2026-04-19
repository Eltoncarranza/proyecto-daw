package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "citas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"fecha", "hora_inicio", "doctor_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita extends BaseEntity {

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
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(length = 500)
    private String motivo;

    @Column(length = 500)
    private String notas;

    // Si derivó en consulta
    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    public enum EstadoCita {
        PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA, NO_ASISTIO
    }
}
