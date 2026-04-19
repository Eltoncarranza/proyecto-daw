package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.dto.GestanteMapper;
import com.gineco.api.entity.*;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GestanteService {

    private final PacienteGestanteRepository gestanteRepo;
    private final PacienteRepository pacienteRepo;
    private final GestanteMapper gestanteMapper; // Inyectado

    private static final Map<String, String> DESCRIPCION_ECOGRAFIAS = Map.of(
            "ECO_TEMPRANA", "Ecografía de datación (< 11 semanas)",
            "ECO_PRIMER_TRIMESTRE", "Ecografía de tamizaje genético (11-14 sem)",
            "ECO_MORFOLOGICA", "Ecografía morfológica estructural (18-24 sem)",
            "ECO_TERCER_TRIMESTRE", "Ecografía de tercer trimestre (28-32 sem)",
            "ECO_FINAL", "Ecografía de término (≥36 sem)",
            "NINGUNA_URGENTE", "Sin ecografía urgente en este período"
    );

    public GestanteResponse obtener(Long pacienteId) {
        PacienteGestante g = gestanteRepo.findByPacienteId(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Datos de embarazo no encontrados"));
        GestanteResponse response = gestanteMapper.toResponse(g);
        response.setDescripcionEcografia(DESCRIPCION_ECOGRAFIAS.getOrDefault(response.getRecomendacionEcografia(), ""));
        return response;
    }

    @Transactional
    public GestanteResponse crear(Long pacienteId, GestanteRequest request) {
        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", pacienteId));

        if (gestanteRepo.existsByPacienteId(pacienteId)) {
            throw new BusinessException("Esta paciente ya tiene datos de embarazo registrados");
        }

        paciente.setTipoPaciente(Paciente.TipoPaciente.GESTANTE);
        pacienteRepo.save(paciente);

        PacienteGestante g = gestanteMapper.toEntity(request);
        g.setPaciente(paciente);
        return toGestanteResponse(gestanteRepo.save(g));
    }

    private GestanteResponse toGestanteResponse(PacienteGestante g) {
        GestanteResponse res = gestanteMapper.toResponse(g);
        res.setDescripcionEcografia(DESCRIPCION_ECOGRAFIAS.getOrDefault(res.getRecomendacionEcografia(), ""));
        return res;
    }
}