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

    Page<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId, Pageable pageable);

    List<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);

    // Consultas del día para el doctor
    @Query("SELECT c FROM Consulta c WHERE c.doctor.id = :doctorId " +
           "AND c.fechaConsulta >= :inicio AND c.fechaConsulta < :fin " +
           "ORDER BY c.fechaConsulta ASC")
    List<Consulta> findConsultasDelDia(
        @Param("doctorId") Long doctorId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    long countByPacienteId(Long pacienteId);
}
