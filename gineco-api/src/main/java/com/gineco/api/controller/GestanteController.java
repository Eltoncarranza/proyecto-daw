package com.gineco.api.controller;

import com.gineco.api.dto.GinecoDTOs.*;
import com.gineco.api.service.GestanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/gestante")
@RequiredArgsConstructor
@Tag(name = "Embarazo", description = "Control obstétrico y seguimiento del embarazo")
public class GestanteController {

    private final GestanteService gestanteService;

    @GetMapping
    @Operation(summary = "Obtener datos del embarazo de la paciente")
    public ResponseEntity<GestanteResponse> obtener(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(gestanteService.obtener(pacienteId));
    }

    @PostMapping
    @Operation(summary = "Registrar datos de embarazo (convierte a paciente gestante)")
    public ResponseEntity<GestanteResponse> crear(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GestanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(gestanteService.crear(pacienteId, request));
    }

    @PutMapping
    @Operation(summary = "Actualizar datos del embarazo")
    public ResponseEntity<GestanteResponse> actualizar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GestanteRequest request) {
        return ResponseEntity.ok(gestanteService.actualizar(pacienteId, request));
    }
}
