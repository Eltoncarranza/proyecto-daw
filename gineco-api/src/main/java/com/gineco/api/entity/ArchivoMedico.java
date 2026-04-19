package com.gineco.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "archivos_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoMedico extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @Column(nullable = false, length = 255)
    private String nombreOriginal;

    @Column(nullable = false, length = 500)
    private String urlArchivo; // URL en Supabase Storage

    @Column(nullable = false, length = 100)
    private String storageKey; // clave en Supabase Storage

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArchivo tipoArchivo;

    @Column(length = 100)
    private String contentType;

    private Long tamanoBytes;

    @Column(length = 500)
    private String descripcion;

    public enum TipoArchivo {
        ECOGRAFIA, LABORATORIO, PDF, IMAGEN, OTRO
    }
}
