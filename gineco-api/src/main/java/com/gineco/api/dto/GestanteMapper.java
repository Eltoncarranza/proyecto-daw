package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.GestanteRequest;
import com.gineco.api.dto.GinecoDTOs.GestanteResponse;
import com.gineco.api.entity.PacienteGestante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GestanteMapper {

    @Mapping(target = "pacienteId", source = "g.paciente.id")
    @Mapping(target = "semanasActuales", expression = "java(g.calcularSemanasActuales())")
    @Mapping(target = "fechaProbableParto", expression = "java(g.calcularFPP())")
    @Mapping(target = "recomendacionEcografia", expression = "java(g.recomendarEcografia())")
    @Mapping(target = "descripcionEcografia", ignore = true) // Se llena en el service con el Map de descripciones
    GestanteResponse toResponse(PacienteGestante g);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "activo", ignore = true)
    PacienteGestante toEntity(GestanteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    void updateEntityFromRequest(GestanteRequest request, @MappingTarget PacienteGestante gestante);
}