package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.dto.ConsultaMapper;
import com.gineco.api.entity.*;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepo;
    private final PacienteRepository pacienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final ArchivoMedicoRepository archivoRepo;
    private final SupabaseStorageService storageService;
    private final ConsultaMapper consultaMapper; // Inyectado

    public Page<ConsultaResponse> listarPorPaciente(Long pacienteId, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        return consultaRepo.findByPacienteIdOrderByFechaConsultaDesc(pacienteId, pageable)
                .map(consultaMapper::toResponse);
    }

    @Transactional
    public ConsultaResponse crear(ConsultaRequest request, String doctorUsername) {
        Paciente paciente = pacienteRepo.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", request.getPacienteId()));
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        Consulta c = consultaMapper.toEntity(request);
        c.setPaciente(paciente);
        c.setDoctor(doctor);
        c.setFechaConsulta(LocalDateTime.now());
        return consultaMapper.toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ConsultaResponse actualizar(Long id, ConsultaRequest request) {
        Consulta c = findById(id);
        consultaMapper.updateEntityFromRequest(request, c);
        return consultaMapper.toResponse(consultaRepo.save(c));
    }

    private Consulta findById(Long id) {
        return consultaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
    }
}