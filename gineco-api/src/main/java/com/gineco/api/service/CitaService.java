package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.entity.*;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.*;
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

    public List<CitaResponse> agendaDia(String doctorUsername, LocalDate fecha) {
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        return citaRepo.findAgendaDia(doctor.getId(), fecha)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CitaResponse> citasPaciente(Long pacienteId) {
        return citaRepo.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CitaResponse crear(CitaRequest request, String doctorUsername) {
        validarDuracion(request.getHoraInicio(), request.getHoraFin());
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        validarConflictos(doctor.getId(), request.getFecha(),
            request.getHoraInicio(), request.getHoraFin(), 0L);

        Paciente paciente = pacienteRepo.findById(request.getPacienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", request.getPacienteId()));

        Cita cita = Cita.builder()
            .paciente(paciente).doctor(doctor)
            .fecha(request.getFecha())
            .horaInicio(request.getHoraInicio())
            .horaFin(request.getHoraFin())
            .motivo(request.getMotivo())
            .notas(request.getNotas())
            .build();
        return toResponse(citaRepo.save(cita));
    }

    @Transactional
    public CitaResponse actualizar(Long id, CitaRequest request, String doctorUsername) {
        Cita cita = findById(id);
        validarDuracion(request.getHoraInicio(), request.getHoraFin());
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        validarConflictos(doctor.getId(), request.getFecha(),
            request.getHoraInicio(), request.getHoraFin(), id);

        cita.setFecha(request.getFecha());
        cita.setHoraInicio(request.getHoraInicio());
        cita.setHoraFin(request.getHoraFin());
        cita.setMotivo(request.getMotivo());
        cita.setNotas(request.getNotas());
        return toResponse(citaRepo.save(cita));
    }

    @Transactional
    public CitaResponse cambiarEstado(Long id, Cita.EstadoCita estado) {
        Cita cita = findById(id);
        cita.setEstado(estado);
        return toResponse(citaRepo.save(cita));
    }

    @Transactional
    public void cancelar(Long id) {
        Cita cita = findById(id);
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepo.save(cita);
    }

    private void validarDuracion(LocalTime inicio, LocalTime fin) {
        if (!fin.isAfter(inicio))
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        if (Duration.between(inicio, fin).toMinutes() < 30)
            throw new BusinessException("La duración mínima de una cita es 30 minutos");
    }

    private void validarConflictos(Long doctorId, LocalDate fecha,
                                    LocalTime inicio, LocalTime fin, Long excludeId) {
        List<Cita> solapadas = citaRepo.findCitasSolapadas(doctorId, fecha, inicio, fin, excludeId);
        if (!solapadas.isEmpty()) {
            Cita c = solapadas.get(0);
            throw new BusinessException(
                "Conflicto de horario con la cita de " + c.getPaciente().getNombreCompleto()
                + " a las " + c.getHoraInicio());
        }
    }

    private Cita findById(Long id) {
        return citaRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cita", id));
    }

    private CitaResponse toResponse(Cita c) {
        CitaResponse r = new CitaResponse();
        r.setId(c.getId());
        r.setPacienteId(c.getPaciente().getId());
        r.setPacienteNombre(c.getPaciente().getNombreCompleto());
        r.setPacienteDni(c.getPaciente().getDni());
        r.setFecha(c.getFecha());
        r.setHoraInicio(c.getHoraInicio());
        r.setHoraFin(c.getHoraFin());
        r.setEstado(c.getEstado());
        r.setMotivo(c.getMotivo());
        r.setNotas(c.getNotas());
        r.setAdvertenciaCercana(false);
        return r;
    }
}
