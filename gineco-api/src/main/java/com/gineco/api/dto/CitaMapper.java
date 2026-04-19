package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.CitaRequest;
import com.gineco.api.dto.GinecoDTOs.CitaResponse;
import com.gineco.api.entity.Cita;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CitaMapper {

    @Mapping(target = "pacienteId", source = "c.paciente.id")
    @Mapping(target = "pacienteNombre", expression = "java(c.getPaciente().getNombres() + \" \" + c.getPaciente().getApellidos())")
    @Mapping(target = "pacienteDni", source = "c.paciente.dni")
    CitaResponse toResponse(Cita c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Cita toEntity(CitaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    void updateEntityFromRequest(@Valid CitaRequest request, @MappingTarget Cita cita);
}