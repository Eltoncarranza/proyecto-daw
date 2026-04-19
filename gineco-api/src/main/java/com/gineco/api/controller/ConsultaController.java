package com.gineco.api.controller;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.entity.ArchivoMedico;
import com.gineco.api.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Historial clínico y consultas médicas")
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Historial paginado de una paciente")
    public ResponseEntity<Page<ConsultaResponse>> porPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        return ResponseEntity.ok(consultaService.listarPorPaciente(pacienteId, pagina, tamano));
    }

    @GetMapping("/paciente/{pacienteId}/historial")
    @Operation(summary = "Historial completo de una paciente")
    public ResponseEntity<List<ConsultaResponse>> historialCompleto(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.historialCompleto(pacienteId));
    }

    @GetMapping("/hoy")
    @Operation(summary = "Consultas del día del doctor actual")
    public ResponseEntity<List<ConsultaResponse>> hoy(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(consultaService.consultasDelDia(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> crear(
            @Valid @RequestBody ConsultaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(consultaService.crear(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ConsultaRequest request) {
        return ResponseEntity.ok(consultaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ConsultaResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.finalizar(id));
    }

    @PostMapping("/{consultaId}/archivos")
    public ResponseEntity<ArchivoResponse> subirArchivo(
            @PathVariable Long consultaId,
            @RequestParam("archivo") MultipartFile file,
            @RequestParam(defaultValue = "OTRO") String tipo,
            @RequestParam(required = false) String descripcion) {
        ArchivoMedico.TipoArchivo tipoArchivo = ArchivoMedico.TipoArchivo.valueOf(tipo.toUpperCase());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(consultaService.subirArchivo(consultaId, file, tipoArchivo, descripcion));
    }

    @DeleteMapping("/archivos/{archivoId}")
    public ResponseEntity<Map<String, String>> eliminarArchivo(@PathVariable Long archivoId) {
        consultaService.eliminarArchivo(archivoId);
        return ResponseEntity.ok(Map.of("mensaje", "Archivo eliminado"));
    }
}
