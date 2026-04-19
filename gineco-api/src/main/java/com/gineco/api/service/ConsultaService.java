package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs.*;
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

    public Page<ConsultaResponse> listarPorPaciente(Long pacienteId, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        return consultaRepo.findByPacienteIdOrderByFechaConsultaDesc(pacienteId, pageable)
            .map(this::toResponse);
    }

    public List<ConsultaResponse> historialCompleto(Long pacienteId) {
        return consultaRepo.findByPacienteIdOrderByFechaConsultaDesc(pacienteId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ConsultaResponse obtener(Long id) {
        return toResponse(findById(id));
    }

    public List<ConsultaResponse> consultasDelDia(String doctorUsername) {
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = inicio.plusDays(1);
        return consultaRepo.findConsultasDelDia(doctor.getId(), inicio, fin)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ConsultaResponse crear(ConsultaRequest request, String doctorUsername) {
        Paciente paciente = pacienteRepo.findById(request.getPacienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", request.getPacienteId()));
        Usuario doctor = usuarioRepo.findByUsername(doctorUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        Consulta c = new Consulta();
        c.setPaciente(paciente);
        c.setDoctor(doctor);
        c.setFechaConsulta(LocalDateTime.now());
        mapRequest(request, c);
        return toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ConsultaResponse actualizar(Long id, ConsultaRequest request) {
        Consulta c = findById(id);
        mapRequest(request, c);
        return toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ConsultaResponse finalizar(Long id) {
        Consulta c = findById(id);
        c.setFinalizada(true);
        return toResponse(consultaRepo.save(c));
    }

    @Transactional
    public ArchivoResponse subirArchivo(Long consultaId, MultipartFile file,
                                         ArchivoMedico.TipoArchivo tipo, String descripcion) {
        Consulta consulta = findById(consultaId);
        String storageKey = storageService.upload(file, "consulta-" + consultaId);
        String url = storageService.getPublicUrl(storageKey);

        ArchivoMedico archivo = ArchivoMedico.builder()
            .consulta(consulta)
            .nombreOriginal(file.getOriginalFilename())
            .urlArchivo(url)
            .storageKey(storageKey)
            .tipoArchivo(tipo)
            .contentType(file.getContentType())
            .tamanoBytes(file.getSize())
            .descripcion(descripcion)
            .build();
        return toArchivoResponse(archivoRepo.save(archivo));
    }

    @Transactional
    public void eliminarArchivo(Long archivoId) {
        ArchivoMedico archivo = archivoRepo.findById(archivoId)
            .orElseThrow(() -> new ResourceNotFoundException("Archivo", archivoId));
        storageService.delete(archivo.getStorageKey());
        archivoRepo.delete(archivo);
    }

    private Consulta findById(Long id) {
        return consultaRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
    }

    private void mapRequest(ConsultaRequest r, Consulta c) {
        if (r.getTipoConsulta() != null) c.setTipoConsulta(r.getTipoConsulta());
        c.setMotivoConsulta(r.getMotivoConsulta());
        c.setAnamnesis(r.getAnamnesis());
        c.setExamenFisico(r.getExamenFisico());
        c.setDiagnostico(r.getDiagnostico());
        c.setTratamiento(r.getTratamiento());
        c.setIndicaciones(r.getIndicaciones());
        c.setPresionArterial(r.getPresionArterial());
        c.setTemperatura(r.getTemperatura());
        c.setFrecuenciaCardiaca(r.getFrecuenciaCardiaca());
        c.setPeso(r.getPeso());
        c.setTalla(r.getTalla());
        c.setFcfBebe(r.getFcfBebe());
        c.setAlturaUterina(r.getAlturaUterina());
        c.setHemoglobinaActual(r.getHemoglobinaActual());
        c.setResultadosLaboratorio(r.getResultadosLaboratorio());
        c.setNotasAdicionales(r.getNotasAdicionales());
    }

    public ConsultaResponse toResponse(Consulta c) {
        ConsultaResponse r = new ConsultaResponse();
        r.setId(c.getId());
        r.setPacienteId(c.getPaciente().getId());
        r.setPacienteNombre(c.getPaciente().getNombreCompleto());
        r.setDoctorNombre(c.getDoctor().getNombreCompleto());
        r.setFechaConsulta(c.getFechaConsulta());
        r.setTipoConsulta(c.getTipoConsulta());
        r.setMotivoConsulta(c.getMotivoConsulta());
        r.setAnamnesis(c.getAnamnesis());
        r.setExamenFisico(c.getExamenFisico());
        r.setDiagnostico(c.getDiagnostico());
        r.setTratamiento(c.getTratamiento());
        r.setIndicaciones(c.getIndicaciones());
        r.setPresionArterial(c.getPresionArterial());
        r.setTemperatura(c.getTemperatura());
        r.setFrecuenciaCardiaca(c.getFrecuenciaCardiaca());
        r.setPeso(c.getPeso());
        r.setFcfBebe(c.getFcfBebe());
        r.setAlturaUterina(c.getAlturaUterina());
        r.setHemoglobinaActual(c.getHemoglobinaActual());
        r.setResultadosLaboratorio(c.getResultadosLaboratorio());
        r.setNotasAdicionales(c.getNotasAdicionales());
        r.setFinalizada(c.getFinalizada());
        r.setArchivos(c.getArchivos().stream().map(this::toArchivoResponse).collect(Collectors.toList()));
        return r;
    }

    private ArchivoResponse toArchivoResponse(ArchivoMedico a) {
        ArchivoResponse r = new ArchivoResponse();
        r.setId(a.getId());
        r.setNombreOriginal(a.getNombreOriginal());
        r.setUrlArchivo(a.getUrlArchivo());
        r.setTipoArchivo(a.getTipoArchivo());
        r.setDescripcion(a.getDescripcion());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
