package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.entity.Paciente;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.dto.PacienteMapper;
import com.gineco.api.repository.ConsultaRepository;
import com.gineco.api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepo;
    private final ConsultaRepository consultaRepo; // Necesario para contar consultas en el Response
    private final PacienteMapper pacienteMapper;

    public Page<PacienteResponse> listar(String busqueda, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("apellidos").ascending());
        Page<Paciente> page = (busqueda != null && !busqueda.isBlank())
                ? pacienteRepo.buscar(busqueda.trim(), pageable)
                : pacienteRepo.findByActivoTrueOrderByApellidosAsc(pageable);
        return page.map(pacienteMapper::toResponse);
    }

    public PacienteResponse obtener(Long id) {
        Paciente p = findById(id);
        return pacienteMapper.toResponse(p);
    }

    public PacienteResponse obtenerPorDni(String dni) {
        Paciente p = pacienteRepo.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con DNI: " + dni));
        return pacienteMapper.toResponse(p);
    }

    @Transactional
    public PacienteResponse crear(PacienteRequest request) {
        if (pacienteRepo.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un paciente con el DNI: " + request.getDni());
        }
        Paciente p = pacienteMapper.toEntity(request);
        return pacienteMapper.toResponse(pacienteRepo.save(p));
    }

    @Transactional
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        Paciente p = findById(id);
        if (!p.getDni().equals(request.getDni()) && pacienteRepo.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un paciente con el DNI: " + request.getDni());
        }
        pacienteMapper.updateEntityFromRequest(request, p);
        return pacienteMapper.toResponse(pacienteRepo.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        Paciente p = findById(id);
        p.setActivo(false);
        pacienteRepo.save(p);
    }

    @Transactional
    public PacienteResponse cambiarTipo(Long id, Paciente.TipoPaciente tipo) {
        Paciente p = findById(id);
        p.setTipoPaciente(tipo);
        return pacienteMapper.toResponse(pacienteRepo.save(p));
    }

    private Paciente findById(Long id) {
        return pacienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }
}