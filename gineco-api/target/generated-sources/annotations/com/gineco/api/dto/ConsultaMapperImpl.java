package com.gineco.api.dto;

import com.gineco.api.entity.ArchivoMedico;
import com.gineco.api.entity.Consulta;
import com.gineco.api.entity.Paciente;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T18:04:11-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class ConsultaMapperImpl implements ConsultaMapper {

    @Override
    public GinecoDTOs.ConsultaResponse toResponse(Consulta c) {
        if ( c == null ) {
            return null;
        }

        GinecoDTOs.ConsultaResponse consultaResponse = new GinecoDTOs.ConsultaResponse();

        consultaResponse.setPacienteId( cPacienteId( c ) );
        consultaResponse.setId( c.getId() );
        consultaResponse.setFechaConsulta( c.getFechaConsulta() );
        consultaResponse.setTipoConsulta( c.getTipoConsulta() );
        consultaResponse.setMotivoConsulta( c.getMotivoConsulta() );
        consultaResponse.setAnamnesis( c.getAnamnesis() );
        consultaResponse.setExamenFisico( c.getExamenFisico() );
        consultaResponse.setDiagnostico( c.getDiagnostico() );
        consultaResponse.setTratamiento( c.getTratamiento() );
        consultaResponse.setIndicaciones( c.getIndicaciones() );
        consultaResponse.setPresionArterial( c.getPresionArterial() );
        consultaResponse.setTemperatura( c.getTemperatura() );
        consultaResponse.setFrecuenciaCardiaca( c.getFrecuenciaCardiaca() );
        consultaResponse.setPeso( c.getPeso() );
        consultaResponse.setFcfBebe( c.getFcfBebe() );
        consultaResponse.setAlturaUterina( c.getAlturaUterina() );
        consultaResponse.setHemoglobinaActual( c.getHemoglobinaActual() );
        consultaResponse.setResultadosLaboratorio( c.getResultadosLaboratorio() );
        consultaResponse.setNotasAdicionales( c.getNotasAdicionales() );
        consultaResponse.setFinalizada( c.getFinalizada() );
        consultaResponse.setArchivos( archivoMedicoListToArchivoResponseList( c.getArchivos() ) );

        consultaResponse.setPacienteNombre( c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos() );
        consultaResponse.setDoctorNombre( c.getDoctor().getNombreCompleto() );

        return consultaResponse;
    }

    @Override
    public Consulta toEntity(GinecoDTOs.ConsultaRequest request) {
        if ( request == null ) {
            return null;
        }

        Consulta consulta = new Consulta();

        consulta.setTipoConsulta( request.getTipoConsulta() );
        consulta.setMotivoConsulta( request.getMotivoConsulta() );
        consulta.setAnamnesis( request.getAnamnesis() );
        consulta.setExamenFisico( request.getExamenFisico() );
        consulta.setDiagnostico( request.getDiagnostico() );
        consulta.setTratamiento( request.getTratamiento() );
        consulta.setIndicaciones( request.getIndicaciones() );
        consulta.setPresionArterial( request.getPresionArterial() );
        consulta.setTemperatura( request.getTemperatura() );
        consulta.setFrecuenciaCardiaca( request.getFrecuenciaCardiaca() );
        consulta.setPeso( request.getPeso() );
        consulta.setTalla( request.getTalla() );
        consulta.setFcfBebe( request.getFcfBebe() );
        consulta.setAlturaUterina( request.getAlturaUterina() );
        consulta.setHemoglobinaActual( request.getHemoglobinaActual() );
        consulta.setResultadosLaboratorio( request.getResultadosLaboratorio() );
        consulta.setNotasAdicionales( request.getNotasAdicionales() );

        return consulta;
    }

    @Override
    public void updateEntityFromRequest(GinecoDTOs.ConsultaRequest request, Consulta consulta) {
        if ( request == null ) {
            return;
        }

        consulta.setTipoConsulta( request.getTipoConsulta() );
        consulta.setMotivoConsulta( request.getMotivoConsulta() );
        consulta.setAnamnesis( request.getAnamnesis() );
        consulta.setExamenFisico( request.getExamenFisico() );
        consulta.setDiagnostico( request.getDiagnostico() );
        consulta.setTratamiento( request.getTratamiento() );
        consulta.setIndicaciones( request.getIndicaciones() );
        consulta.setPresionArterial( request.getPresionArterial() );
        consulta.setTemperatura( request.getTemperatura() );
        consulta.setFrecuenciaCardiaca( request.getFrecuenciaCardiaca() );
        consulta.setPeso( request.getPeso() );
        consulta.setTalla( request.getTalla() );
        consulta.setFcfBebe( request.getFcfBebe() );
        consulta.setAlturaUterina( request.getAlturaUterina() );
        consulta.setHemoglobinaActual( request.getHemoglobinaActual() );
        consulta.setResultadosLaboratorio( request.getResultadosLaboratorio() );
        consulta.setNotasAdicionales( request.getNotasAdicionales() );
    }

    @Override
    public GinecoDTOs.ArchivoResponse toArchivoResponse(ArchivoMedico a) {
        if ( a == null ) {
            return null;
        }

        GinecoDTOs.ArchivoResponse archivoResponse = new GinecoDTOs.ArchivoResponse();

        archivoResponse.setId( a.getId() );
        archivoResponse.setNombreOriginal( a.getNombreOriginal() );
        archivoResponse.setUrlArchivo( a.getUrlArchivo() );
        archivoResponse.setTipoArchivo( a.getTipoArchivo() );
        archivoResponse.setDescripcion( a.getDescripcion() );
        archivoResponse.setCreatedAt( a.getCreatedAt() );

        return archivoResponse;
    }

    private Long cPacienteId(Consulta consulta) {
        if ( consulta == null ) {
            return null;
        }
        Paciente paciente = consulta.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        Long id = paciente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<GinecoDTOs.ArchivoResponse> archivoMedicoListToArchivoResponseList(List<ArchivoMedico> list) {
        if ( list == null ) {
            return null;
        }

        List<GinecoDTOs.ArchivoResponse> list1 = new ArrayList<GinecoDTOs.ArchivoResponse>( list.size() );
        for ( ArchivoMedico archivoMedico : list ) {
            list1.add( toArchivoResponse( archivoMedico ) );
        }

        return list1;
    }
}
