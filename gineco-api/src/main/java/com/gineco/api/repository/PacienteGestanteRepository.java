package com.gineco.api.repository;

import com.gineco.api.entity.PacienteGestante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteGestanteRepository extends JpaRepository<PacienteGestante, Long> {
    Optional<PacienteGestante> findByPacienteId(Long pacienteId);
    boolean existsByPacienteId(Long pacienteId);
}
