package com.gineco.api.dto;

import com.gineco.api.dto.GinecoDTOs.ConsultaRequest;
import com.gineco.api.dto.GinecoDTOs.ConsultaResponse;
import com.gineco.api.dto.GinecoDTOs.ArchivoResponse;
import com.gineco.api.entity.Consulta;
import com.gineco.api.entity.ArchivoMedico;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ConsultaMapper {

    @Mapping(target = "pacienteId", source = "c.paciente.id")
    @Mapping(target = "pacienteNombre", expression = "java(c.getPaciente().getNombres() + \" \" + c.getPaciente().getApellidos())")
    @Mapping(target = "doctorNombre", expression = "java(c.getDoctor().getNombreCompleto())")
    ConsultaResponse toResponse(Consulta c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    Consulta toEntity(ConsultaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    void updateEntityFromRequest(ConsultaRequest request, @MappingTarget Consulta consulta);

    ArchivoResponse toArchivoResponse(ArchivoMedico a);
}