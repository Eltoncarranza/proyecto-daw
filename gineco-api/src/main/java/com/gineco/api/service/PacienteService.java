package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.entity.Paciente;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.ConsultaRepository;
import com.gineco.api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepo;
    private final ConsultaRepository consultaRepo;

    public Page<PacienteResponse> listar(String busqueda, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("apellidos").ascending());
        Page<Paciente> page = (busqueda != null && !busqueda.isBlank())
            ? pacienteRepo.buscar(busqueda.trim(), pageable)
            : pacienteRepo.findByActivoTrueOrderByApellidosAsc(pageable);
        return page.map(this::toResponse);
    }

    public PacienteResponse obtener(Long id) {
        return toResponse(findById(id));
    }

    public PacienteResponse obtenerPorDni(String dni) {
        Paciente p = pacienteRepo.findByDni(dni)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con DNI: " + dni));
        return toResponse(p);
    }

    @Transactional
    public PacienteResponse crear(PacienteRequest request) {
        if (pacienteRepo.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un paciente con el DNI: " + request.getDni());
        }
        Paciente p = new Paciente();
        mapRequestToEntity(request, p);
        return toResponse(pacienteRepo.save(p));
    }

    @Transactional
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        Paciente p = findById(id);
        if (!p.getDni().equals(request.getDni()) && pacienteRepo.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un paciente con el DNI: " + request.getDni());
        }
        mapRequestToEntity(request, p);
        return toResponse(pacienteRepo.save(p));
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
        return toResponse(pacienteRepo.save(p));
    }

    public Paciente findById(Long id) {
        return pacienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }

    private void mapRequestToEntity(PacienteRequest r, Paciente p) {
        p.setDni(r.getDni());
        p.setNombres(r.getNombres());
        p.setApellidos(r.getApellidos());
        p.setFechaNacimiento(r.getFechaNacimiento());
        p.setTelefono(r.getTelefono());
        p.setDireccion(r.getDireccion());
        p.setEmail(r.getEmail());
        p.setGrupoSanguineo(r.getGrupoSanguineo());
        p.setAlergias(r.getAlergias());
        p.setAntecedentesPersonales(r.getAntecedentesPersonales());
        p.setAntecedentesFamiliares(r.getAntecedentesFamiliares());
        if (r.getTipoPaciente() != null) p.setTipoPaciente(r.getTipoPaciente());
        p.setContactoEmergenciaNombre(r.getContactoEmergenciaNombre());
        p.setContactoEmergenciaTelefono(r.getContactoEmergenciaTelefono());
        p.setContactoEmergenciaRelacion(r.getContactoEmergenciaRelacion());
    }

    public PacienteResponse toResponse(Paciente p) {
        PacienteResponse r = new PacienteResponse();
        r.setId(p.getId());
        r.setDni(p.getDni());
        r.setNombres(p.getNombres());
        r.setApellidos(p.getApellidos());
        r.setNombreCompleto(p.getNombreCompleto());
        r.setFechaNacimiento(p.getFechaNacimiento());
        if (p.getFechaNacimiento() != null) {
            r.setEdad(Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears());
        }
        r.setTelefono(p.getTelefono());
        r.setEmail(p.getEmail());
        r.setGrupoSanguineo(p.getGrupoSanguineo());
        r.setAlergias(p.getAlergias());
        r.setAntecedentesPersonales(p.getAntecedentesPersonales());
        r.setAntecedentesFamiliares(p.getAntecedentesFamiliares());
        r.setTipoPaciente(p.getTipoPaciente());
        r.setContactoEmergenciaNombre(p.getContactoEmergenciaNombre());
        r.setContactoEmergenciaTelefono(p.getContactoEmergenciaTelefono());
        r.setTotalConsultas(consultaRepo.countByPacienteId(p.getId()));
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
