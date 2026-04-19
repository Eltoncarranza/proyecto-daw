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
    private final ConsultaMapper consultaMapper;

    public Page<ConsultaResponse> listarPorPaciente(Long pacienteId, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        return consultaRepo.findByPacienteIdOrderByFechaConsultaDesc(pacienteId, pageable)
                .map(consultaMapper::toResponse);
    }

    public List<ConsultaResponse> historialCompleto(Long pacienteId) {
        return consultaRepo.findByPacienteIdOrderByFechaConsultaDesc(pacienteId)
                .stream().map(consultaMapper::toResponse).collect(Collectors.toList());
    }

    public List<ConsultaResponse> consultasDelDia(String username) {
        Usuario doctor = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59);
        return consultaRepo.findByDoctorIdAndFechaConsultaBetween(doctor.getId(), inicio, fin)
                .stream().map(consultaMapper::toResponse).collect(Collectors.toList());
    }

    public ConsultaResponse obtener(Long id) {
        return consultaMapper.toResponse(findById(id));
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
        c.setFinalizada(false);
        return consultaMapper.toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ConsultaResponse actualizar(Long id, ConsultaRequest request) {
        Consulta c = findById(id);
        consultaMapper.updateEntityFromRequest(request, c);
        return consultaMapper.toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ConsultaResponse finalizar(Long id) {
        Consulta c = findById(id);
        c.setFinalizada(true);
        return consultaMapper.toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ArchivoResponse subirArchivo(Long consultaId, MultipartFile file, ArchivoMedico.TipoArchivo tipo, String desc) {
        Consulta consulta = findById(consultaId);
        String url = storageService.uploadFile(file);

        ArchivoMedico archivo = new ArchivoMedico();
        archivo.setConsulta(consulta);
        archivo.setNombreOriginal(file.getOriginalFilename());
        archivo.setUrlArchivo(url);
        archivo.setTipoArchivo(tipo);
        archivo.setDescripcion(desc);

        return consultaMapper.toArchivoResponse(archivoRepo.save(archivo));
    }

    @Transactional
    public void eliminarArchivo(Long archivoId) {
        ArchivoMedico archivo = archivoRepo.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado"));
        storageService.deleteFile(archivo.getUrlArchivo());
        archivoRepo.delete(archivo);
    }

    private Consulta findById(Long id) {
        return consultaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
    }
}