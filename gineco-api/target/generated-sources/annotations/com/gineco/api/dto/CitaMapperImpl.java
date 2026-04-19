package com.gineco.api.dto;

import com.gineco.api.entity.Cita;
import com.gineco.api.entity.Paciente;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T18:04:11-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class CitaMapperImpl implements CitaMapper {

    @Override
    public GinecoDTOs.CitaResponse toResponse(Cita c) {
        if ( c == null ) {
            return null;
        }

        GinecoDTOs.CitaResponse citaResponse = new GinecoDTOs.CitaResponse();

        citaResponse.setPacienteId( cPacienteId( c ) );
        citaResponse.setPacienteDni( cPacienteDni( c ) );
        citaResponse.setId( c.getId() );
        citaResponse.setFecha( c.getFecha() );
        citaResponse.setHoraInicio( c.getHoraInicio() );
        citaResponse.setHoraFin( c.getHoraFin() );
        citaResponse.setEstado( c.getEstado() );
        citaResponse.setMotivo( c.getMotivo() );
        citaResponse.setNotas( c.getNotas() );

        citaResponse.setPacienteNombre( c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos() );

        return citaResponse;
    }

    @Override
    public Cita toEntity(GinecoDTOs.CitaRequest request) {
        if ( request == null ) {
            return null;
        }

        Cita.CitaBuilder cita = Cita.builder();

        cita.fecha( request.getFecha() );
        cita.horaInicio( request.getHoraInicio() );
        cita.horaFin( request.getHoraFin() );
        cita.motivo( request.getMotivo() );
        cita.notas( request.getNotas() );

        return cita.build();
    }

    @Override
    public void updateEntityFromRequest(GinecoDTOs.CitaRequest request, Cita cita) {
        if ( request == null ) {
            return;
        }

        cita.setFecha( request.getFecha() );
        cita.setHoraInicio( request.getHoraInicio() );
        cita.setHoraFin( request.getHoraFin() );
        cita.setMotivo( request.getMotivo() );
        cita.setNotas( request.getNotas() );
    }

    private Long cPacienteId(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Paciente paciente = cita.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        Long id = paciente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String cPacienteDni(Cita cita) {
        if ( cita == null ) {
            return null;
        }
        Paciente paciente = cita.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        String dni = paciente.getDni();
        if ( dni == null ) {
            return null;
        }
        return dni;
    }
}
