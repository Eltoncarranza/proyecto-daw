package com.gineco.api.repository;

import com.gineco.api.entity.ArchivoMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoMedicoRepository extends JpaRepository<ArchivoMedico, Long> {
    List<ArchivoMedico> findByConsultaId(Long consultaId);
    List<ArchivoMedico> findByConsultaIdAndTipoArchivo(Long consultaId, ArchivoMedico.TipoArchivo tipo);
}
