package com.gineco.api.repository;

import com.gineco.api.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaAndDoctorIdOrderByHoraInicioAsc(LocalDate fecha, Long doctorId);

    // Detectar conflicto de horario (cita que se solapa)
    @Query("SELECT c FROM Cita c WHERE c.doctor.id = :doctorId " +
           "AND c.fecha = :fecha " +
           "AND c.estado NOT IN ('CANCELADA', 'NO_ASISTIO') " +
           "AND c.id <> :excludeId " +
           "AND (c.horaInicio < :horaFin AND c.horaFin > :horaInicio)")
    List<Cita> findCitasSolapadas(
        @Param("doctorId") Long doctorId,
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin,
        @Param("excludeId") Long excludeId
    );

    // Citas próximas (para alertas)
    @Query("SELECT c FROM Cita c WHERE c.doctor.id = :doctorId " +
           "AND c.fecha = :fecha " +
           "AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "ORDER BY c.horaInicio ASC")
    List<Cita> findAgendaDia(@Param("doctorId") Long doctorId, @Param("fecha") LocalDate fecha);

    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);
}
