package com.gineco.api.dto;

import com.gineco.api.entity.Paciente;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T18:04:11-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class PacienteMapperImpl implements PacienteMapper {

    @Override
    public GinecoDTOs.PacienteResponse toResponse(Paciente p) {
        if ( p == null ) {
            return null;
        }

        GinecoDTOs.PacienteResponse pacienteResponse = new GinecoDTOs.PacienteResponse();

        pacienteResponse.setId( p.getId() );
        pacienteResponse.setDni( p.getDni() );
        pacienteResponse.setNombres( p.getNombres() );
        pacienteResponse.setApellidos( p.getApellidos() );
        pacienteResponse.setFechaNacimiento( p.getFechaNacimiento() );
        pacienteResponse.setTelefono( p.getTelefono() );
        pacienteResponse.setEmail( p.getEmail() );
        pacienteResponse.setGrupoSanguineo( p.getGrupoSanguineo() );
        pacienteResponse.setAlergias( p.getAlergias() );
        pacienteResponse.setTipoPaciente( p.getTipoPaciente() );
        pacienteResponse.setCreatedAt( p.getCreatedAt() );

        pacienteResponse.setNombreCompleto( p.getNombres() + " " + p.getApellidos() );
        pacienteResponse.setEdad( calcularEdad(p.getFechaNacimiento()) );

        return pacienteResponse;
    }

    @Override
    public Paciente toEntity(GinecoDTOs.PacienteRequest request) {
        if ( request == null ) {
            return null;
        }

        Paciente.PacienteBuilder paciente = Paciente.builder();

        paciente.dni( request.getDni() );
        paciente.nombres( request.getNombres() );
        paciente.apellidos( request.getApellidos() );
        paciente.fechaNacimiento( request.getFechaNacimiento() );
        paciente.telefono( request.getTelefono() );
        paciente.direccion( request.getDireccion() );
        paciente.email( request.getEmail() );
        paciente.grupoSanguineo( request.getGrupoSanguineo() );
        paciente.alergias( request.getAlergias() );
        paciente.tipoPaciente( request.getTipoPaciente() );

        return paciente.build();
    }

    @Override
    public void updateEntityFromRequest(GinecoDTOs.PacienteRequest request, Paciente paciente) {
        if ( request == null ) {
            return;
        }

        paciente.setDni( request.getDni() );
        paciente.setNombres( request.getNombres() );
        paciente.setApellidos( request.getApellidos() );
        paciente.setFechaNacimiento( request.getFechaNacimiento() );
        paciente.setTelefono( request.getTelefono() );
        paciente.setDireccion( request.getDireccion() );
        paciente.setEmail( request.getEmail() );
        paciente.setGrupoSanguineo( request.getGrupoSanguineo() );
        paciente.setAlergias( request.getAlergias() );
        paciente.setTipoPaciente( request.getTipoPaciente() );
    }
}
