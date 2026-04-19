package com.gineco.api.controller;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.entity.Paciente;
import com.gineco.api.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gestión de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    @Operation(summary = "Listar y buscar pacientes")
    public ResponseEntity<Page<PacienteResponse>> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(pacienteService.listar(busqueda, pagina, tamano));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<PacienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtener(id));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar paciente por DNI")
    public ResponseEntity<PacienteResponse> porDni(@PathVariable String dni) {
        return ResponseEntity.ok(pacienteService.obtenerPorDni(dni));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva paciente")
    public ResponseEntity<PacienteResponse> crear(
            @Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de paciente")
    public ResponseEntity<PacienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.ok(pacienteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/tipo")
    @Operation(summary = "Cambiar tipo de paciente (ginecológica / gestante)")
    public ResponseEntity<PacienteResponse> cambiarTipo(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Paciente.TipoPaciente tipo = Paciente.TipoPaciente.valueOf(body.get("tipo").toUpperCase());
        return ResponseEntity.ok(pacienteService.cambiarTipo(id, tipo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja paciente (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
