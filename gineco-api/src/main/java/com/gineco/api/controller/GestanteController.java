package com.gineco.api.controller;

import com.gineco.api.dto.GinecoDTOs; // Importamos la clase principal
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
    public ResponseEntity<GinecoDTOs.GestanteResponse> obtener(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(gestanteService.obtener(pacienteId));
    }

    @PostMapping
    @Operation(summary = "Registrar datos de embarazo (convierte a paciente gestante)")
    public ResponseEntity<GinecoDTOs.GestanteResponse> crear(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GinecoDTOs.GestanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gestanteService.crear(pacienteId, request));
    }

    @PutMapping
    @Operation(summary = "Actualizar datos del embarazo")
    public ResponseEntity<GinecoDTOs.GestanteResponse> actualizar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GinecoDTOs.GestanteRequest request) {
        return ResponseEntity.ok(gestanteService.actualizar(pacienteId, request));
    }
}