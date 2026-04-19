package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.GestanteRequest;
import com.gineco.api.dto.GinecoDTOs.GestanteResponse;
import com.gineco.api.entity.PacienteGestante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GestanteMapper {

    @Mapping(target = "pacienteId", source = "g.paciente.id")
    @Mapping(target = "semanasActuales", expression = "java(g.getSemanasActuales())")
    @Mapping(target = "recomendacionEcografia", expression = "java(g.getRecomendacionEcografia())")
    GestanteResponse toResponse(PacienteGestante g);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    PacienteGestante toEntity(GestanteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    void updateEntityFromRequest(GestanteRequest request, @MappingTarget PacienteGestante gestante);
}