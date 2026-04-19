package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.ConsultaRequest;
import com.gineco.api.dto.GinecoDTOs.ConsultaResponse;
import com.gineco.api.dto.GinecoDTOs.ArchivoResponse;
import com.gineco.api.entity.Consulta;
import com.gineco.api.entity.ArchivoMedico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    @Mapping(target = "pacienteId", source = "paciente.id")
    @Mapping(target = "pacienteNombre", expression = "java(c.getPaciente().getNombreCompleto())")
    @Mapping(target = "doctorNombre", expression = "java(c.getDoctor().getNombreCompleto())")
    ConsultaResponse toResponse(Consulta c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "fechaConsulta", ignore = true)
    @Mapping(target = "finalizada", ignore = true)
    @Mapping(target = "archivos", ignore = true)
    Consulta toEntity(ConsultaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    void updateEntityFromRequest(ConsultaRequest request, @MappingTarget Consulta consulta);

    ArchivoResponse toArchivoResponse(ArchivoMedico a);
}