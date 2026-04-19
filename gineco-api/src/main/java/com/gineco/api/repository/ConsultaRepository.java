package com.gineco.api.repository;

import com.gineco.api.entity.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Para el historial paginado
    Page<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId, Pageable pageable);

    // Para el historial completo
    List<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);

    // Este es el método que usa tu ConsultaService para las consultas del día
    List<Consulta> findByDoctorIdAndFechaConsultaBetween(Long doctorId, LocalDateTime inicio, LocalDateTime fin);

    // Consultas del día con Query personalizada (opcional, pero útil para ordenar)
    @Query("SELECT c FROM Consulta c WHERE c.doctor.id = :doctorId " +
            "AND c.fechaConsulta >= :inicio AND c.fechaConsulta <= :fin " +
            "ORDER BY c.fechaConsulta ASC")
    List<Consulta> findConsultasDelDia(
            @Param("doctorId") Long doctorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    long countByPacienteId(Long pacienteId);
}