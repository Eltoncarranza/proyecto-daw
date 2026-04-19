package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.CitaRequest;
import com.gineco.api.dto.GinecoDTOs.CitaResponse;
import com.gineco.api.entity.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(target = "pacienteId", source = "paciente.id")
    @Mapping(target = "pacienteNombre", expression = "java(c.getPaciente().getNombreCompleto())")
    @Mapping(target = "pacienteDni", source = "paciente.dni")
    CitaResponse toResponse(Cita c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Cita toEntity(CitaRequest request);
}