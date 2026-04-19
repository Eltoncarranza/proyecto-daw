package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
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

    private static final Map<String, String> DESCRIPCION_ECOGRAFIAS = Map.of(
        "ECO_TEMPRANA",         "Ecografía de datación (< 11 semanas): confirma embarazo y fecha probable de parto",
        "ECO_PRIMER_TRIMESTRE", "Ecografía de tamizaje genético (11-14 sem): mide translucencia nucal y marcadores cromosómicos",
        "ECO_MORFOLOGICA",      "Ecografía morfológica estructural (18-24 sem): evaluación detallada de órganos fetales",
        "ECO_TERCER_TRIMESTRE", "Ecografía de tercer trimestre (28-32 sem): evaluación de crecimiento y bienestar fetal",
        "ECO_FINAL",            "Ecografía de término (≥36 sem): posición, peso estimado y madurez placentaria",
        "NINGUNA_URGENTE",      "Sin ecografía urgente en este período, continuar controles habituales"
    );

    public GestanteResponse obtener(Long pacienteId) {
        PacienteGestante g = gestanteRepo.findByPacienteId(pacienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Datos de embarazo no encontrados"));
        return toResponse(g);
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

        PacienteGestante g = new PacienteGestante();
        g.setPaciente(paciente);
        mapRequest(request, g);
        return toResponse(gestanteRepo.save(g));
    }

    @Transactional
    public GestanteResponse actualizar(Long pacienteId, GestanteRequest request) {
        PacienteGestante g = gestanteRepo.findByPacienteId(pacienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Datos de embarazo no encontrados"));
        mapRequest(request, g);
        return toResponse(gestanteRepo.save(g));
    }

    private void mapRequest(GestanteRequest r, PacienteGestante g) {
        g.setFechaUltimaRegla(r.getFechaUltimaRegla());
        g.setFechaUltimaEcografia(r.getFechaUltimaEcografia());
        g.setSemanasEcografia(r.getSemanasEcografia());
        g.setFechaProbableParto(r.getFechaProbableParto());
        g.setGestaciones(r.getGestaciones());
        g.setPartos(r.getPartos());
        g.setCesareas(r.getCesareas());
        g.setAbortos(r.getAbortos());
        g.setGrupoSanguineo(r.getGrupoSanguineo());
        if (r.getRhNegativo() != null) g.setRhNegativo(r.getRhNegativo());
        g.setPesoInicial(r.getPesoInicial());
        g.setTalla(r.getTalla());
        g.setFactoresRiesgo(r.getFactoresRiesgo());
        if (r.getEmbarazoAltoRiesgo() != null) g.setEmbarazoAltoRiesgo(r.getEmbarazoAltoRiesgo());
        g.setHemoglobinaInicial(r.getHemoglobinaInicial());
        g.setHematocritoInicial(r.getHematocritoInicial());
        g.setVihResultado(r.getVihResultado());
        g.setSifilisCruda(r.getSifilisCruda());
        g.setHepatitisBResultado(r.getHepatitisBResultado());
        g.setNotasGenerales(r.getNotasGenerales());
    }

    public GestanteResponse toResponse(PacienteGestante g) {
        GestanteResponse r = new GestanteResponse();
        r.setId(g.getId());
        r.setPacienteId(g.getPaciente().getId());
        r.setFechaUltimaRegla(g.getFechaUltimaRegla());
        r.setFechaUltimaEcografia(g.getFechaUltimaEcografia());
        r.setSemanasActuales(g.calcularSemanasActuales());
        r.setFechaProbableParto(g.calcularFPP());
        String eco = g.recomendarEcografia();
        r.setRecomendacionEcografia(eco);
        r.setDescripcionEcografia(DESCRIPCION_ECOGRAFIAS.getOrDefault(eco, ""));
        r.setGestaciones(g.getGestaciones());
        r.setPartos(g.getPartos());
        r.setCesareas(g.getCesareas());
        r.setAbortos(g.getAbortos());
        r.setGrupoSanguineo(g.getGrupoSanguineo());
        r.setRhNegativo(g.getRhNegativo());
        r.setPesoInicial(g.getPesoInicial());
        r.setTalla(g.getTalla());
        r.setFactoresRiesgo(g.getFactoresRiesgo());
        r.setEmbarazoAltoRiesgo(g.getEmbarazoAltoRiesgo());
        r.setHemoglobinaInicial(g.getHemoglobinaInicial());
        r.setNotasGenerales(g.getNotasGenerales());
        return r;
    }
}
