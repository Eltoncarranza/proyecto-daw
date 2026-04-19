package com.gineco.api.repository;

import com.gineco.api.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    boolean existsByDni(String dni);

    // Búsqueda por DNI o nombre completo (case-insensitive)
    @Query("SELECT p FROM Paciente p WHERE p.activo = true AND (" +
           "LOWER(p.dni) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Paciente> buscar(@Param("termino") String termino, Pageable pageable);

    // Todos los pacientes activos
    Page<Paciente> findByActivoTrueOrderByApellidosAsc(Pageable pageable);

    // Por tipo
    Page<Paciente> findByTipoPacienteAndActivoTrue(Paciente.TipoPaciente tipo, Pageable pageable);
}
