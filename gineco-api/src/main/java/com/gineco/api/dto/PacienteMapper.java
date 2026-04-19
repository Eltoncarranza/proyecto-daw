package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.PacienteRequest;
import com.gineco.api.dto.GinecoDTOs.PacienteResponse;
import com.gineco.api.entity.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PacienteMapper {

    @Mapping(target = "nombreCompleto", expression = "java(p.getNombres() + \" \" + p.getApellidos())")
    @Mapping(target = "edad", expression = "java(calcularEdad(p.getFechaNacimiento()))")
    PacienteResponse toResponse(Paciente p);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consultas", ignore = true)
    @Mapping(target = "datosGestante", ignore = true)
    Paciente toEntity(PacienteRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(PacienteRequest request, @MappingTarget Paciente paciente);

    default Integer calcularEdad(LocalDate fechaNacimiento) {
        return (fechaNacimiento == null) ? null : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}