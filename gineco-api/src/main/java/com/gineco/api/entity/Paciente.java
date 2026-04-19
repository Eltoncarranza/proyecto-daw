package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificación
    @Column(unique = true, nullable = false, length = 20)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 15)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String email;

    @Column(length = 5)
    private String grupoSanguineo; // A+, A-, B+, B-, O+, O-, AB+, AB-

    @Column(length = 500)
    private String alergias;

    @Column(length = 500)
    private String antecedentesPersonales;

    @Column(length = 500)
    private String antecedentesFamiliares;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoPaciente tipoPaciente = TipoPaciente.GINECOLOGICA;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    // Contacto de emergencia
    @Column(length = 100)
    private String contactoEmergenciaNombre;

    @Column(length = 15)
    private String contactoEmergenciaTelefono;

    @Column(length = 50)
    private String contactoEmergenciaRelacion;

    // Relaciones
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Consulta> consultas = new ArrayList<>();

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PacienteGestante datosGestante;

    public enum TipoPaciente {
        GINECOLOGICA, GESTANTE
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
