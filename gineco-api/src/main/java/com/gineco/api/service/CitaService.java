package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.dto.CitaMapper;
import com.gineco.api.entity.*;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepo;
    private final PacienteRepository pacienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final CitaMapper citaMapper;

    public List<CitaResponse> agendaDia(String doctorUsername, LocalDate fecha) {
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        return citaRepo.findAgendaDia(doctor.getId(), fecha)
                .stream().map(citaMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CitaResponse crear(CitaRequest request, String doctorUsername) {
        validarDuracion(request.getHoraInicio(), request.getHoraFin());
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        validarConflictos(doctor.getId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin(), 0L);

        Paciente paciente = pacienteRepo.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", request.getPacienteId()));

        Cita cita = citaMapper.toEntity(request);
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setEstado(Cita.EstadoCita.PROGRAMADA);

        return citaMapper.toResponse(citaRepo.save(cita));
    }

    // --- MÉTODOS COMPLETADOS PARA SOLUCIONAR EL BUILD FAILED ---

    public List<CitaResponse> citasPaciente(Long pacienteId) {
        return citaRepo.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId)
                .stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CitaResponse actualizar(Long id, @Valid CitaRequest request, String username) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        validarDuracion(request.getHoraInicio(), request.getHoraFin());
        validarConflictos(cita.getDoctor().getId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin(), id);

        citaMapper.updateEntityFromRequest(request, cita);

        return citaMapper.toResponse(citaRepo.save(cita));
    }

    @Transactional
    public CitaResponse cambiarEstado(Long id, Cita.EstadoCita estado) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        cita.setEstado(estado);
        return citaMapper.toResponse(citaRepo.save(cita));
    }

    @Transactional
    public void cancelar(Long id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepo.save(cita);
    }

    // --- VALIDACIONES ---

    private void validarDuracion(LocalTime inicio, LocalTime fin) {
        if (!fin.isAfter(inicio))
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        if (Duration.between(inicio, fin).toMinutes() < 15) // Bajamos a 15 min por flexibilidad
            throw new BusinessException("La duración mínima de una cita es 15 minutos");
    }

    private void validarConflictos(Long doctorId, LocalDate fecha, LocalTime inicio, LocalTime fin, Long excludeId) {
        List<Cita> solapadas = citaRepo.findCitasSolapadas(doctorId, fecha, inicio, fin, excludeId);
        if (!solapadas.isEmpty()) {
            Cita c = solapadas.get(0);
            throw new BusinessException("Conflicto de horario con la cita de " + c.getPaciente().getNombreCompleto());
        }
    }
}