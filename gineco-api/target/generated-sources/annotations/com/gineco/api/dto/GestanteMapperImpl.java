package com.gineco.api.dto;

import com.gineco.api.entity.Paciente;
import com.gineco.api.entity.PacienteGestante;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T18:04:11-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class GestanteMapperImpl implements GestanteMapper {

    @Override
    public GinecoDTOs.GestanteResponse toResponse(PacienteGestante g) {
        if ( g == null ) {
            return null;
        }

        GinecoDTOs.GestanteResponse gestanteResponse = new GinecoDTOs.GestanteResponse();

        gestanteResponse.setPacienteId( gPacienteId( g ) );
        gestanteResponse.setId( g.getId() );
        gestanteResponse.setFechaUltimaRegla( g.getFechaUltimaRegla() );
        gestanteResponse.setFechaUltimaEcografia( g.getFechaUltimaEcografia() );
        gestanteResponse.setFechaProbableParto( g.getFechaProbableParto() );
        gestanteResponse.setGestaciones( g.getGestaciones() );
        gestanteResponse.setPartos( g.getPartos() );
        gestanteResponse.setCesareas( g.getCesareas() );
        gestanteResponse.setAbortos( g.getAbortos() );
        gestanteResponse.setGrupoSanguineo( g.getGrupoSanguineo() );
        gestanteResponse.setRhNegativo( g.getRhNegativo() );
        gestanteResponse.setPesoInicial( g.getPesoInicial() );
        gestanteResponse.setTalla( g.getTalla() );
        gestanteResponse.setFactoresRiesgo( g.getFactoresRiesgo() );
        gestanteResponse.setEmbarazoAltoRiesgo( g.getEmbarazoAltoRiesgo() );
        gestanteResponse.setHemoglobinaInicial( g.getHemoglobinaInicial() );
        gestanteResponse.setNotasGenerales( g.getNotasGenerales() );

        gestanteResponse.setSemanasActuales( g.getSemanasActuales() );
        gestanteResponse.setRecomendacionEcografia( g.getRecomendacionEcografia() );

        return gestanteResponse;
    }

    @Override
    public PacienteGestante toEntity(GinecoDTOs.GestanteRequest request) {
        if ( request == null ) {
            return null;
        }

        PacienteGestante.PacienteGestanteBuilder pacienteGestante = PacienteGestante.builder();

        pacienteGestante.fechaUltimaRegla( request.getFechaUltimaRegla() );
        pacienteGestante.fechaUltimaEcografia( request.getFechaUltimaEcografia() );
        pacienteGestante.semanasEcografia( request.getSemanasEcografia() );
        pacienteGestante.fechaProbableParto( request.getFechaProbableParto() );
        pacienteGestante.gestaciones( request.getGestaciones() );
        pacienteGestante.partos( request.getPartos() );
        pacienteGestante.cesareas( request.getCesareas() );
        pacienteGestante.abortos( request.getAbortos() );
        pacienteGestante.grupoSanguineo( request.getGrupoSanguineo() );
        pacienteGestante.rhNegativo( request.getRhNegativo() );
        pacienteGestante.pesoInicial( request.getPesoInicial() );
        pacienteGestante.talla( request.getTalla() );
        pacienteGestante.factoresRiesgo( request.getFactoresRiesgo() );
        pacienteGestante.embarazoAltoRiesgo( request.getEmbarazoAltoRiesgo() );
        pacienteGestante.hemoglobinaInicial( request.getHemoglobinaInicial() );
        pacienteGestante.hematocritoInicial( request.getHematocritoInicial() );
        pacienteGestante.vihResultado( request.getVihResultado() );
        pacienteGestante.sifilisCruda( request.getSifilisCruda() );
        pacienteGestante.hepatitisBResultado( request.getHepatitisBResultado() );
        pacienteGestante.notasGenerales( request.getNotasGenerales() );

        return pacienteGestante.build();
    }

    @Override
    public void updateEntityFromRequest(GinecoDTOs.GestanteRequest request, PacienteGestante gestante) {
        if ( request == null ) {
            return;
        }

        gestante.setFechaUltimaRegla( request.getFechaUltimaRegla() );
        gestante.setFechaUltimaEcografia( request.getFechaUltimaEcografia() );
        gestante.setSemanasEcografia( request.getSemanasEcografia() );
        gestante.setFechaProbableParto( request.getFechaProbableParto() );
        gestante.setGestaciones( request.getGestaciones() );
        gestante.setPartos( request.getPartos() );
        gestante.setCesareas( request.getCesareas() );
        gestante.setAbortos( request.getAbortos() );
        gestante.setGrupoSanguineo( request.getGrupoSanguineo() );
        gestante.setRhNegativo( request.getRhNegativo() );
        gestante.setPesoInicial( request.getPesoInicial() );
        gestante.setTalla( request.getTalla() );
        gestante.setFactoresRiesgo( request.getFactoresRiesgo() );
        gestante.setEmbarazoAltoRiesgo( request.getEmbarazoAltoRiesgo() );
        gestante.setHemoglobinaInicial( request.getHemoglobinaInicial() );
        gestante.setHematocritoInicial( request.getHematocritoInicial() );
        gestante.setVihResultado( request.getVihResultado() );
        gestante.setSifilisCruda( request.getSifilisCruda() );
        gestante.setHepatitisBResultado( request.getHepatitisBResultado() );
        gestante.setNotasGenerales( request.getNotasGenerales() );
    }

    private Long gPacienteId(PacienteGestante pacienteGestante) {
        if ( pacienteGestante == null ) {
            return null;
        }
        Paciente paciente = pacienteGestante.getPaciente();
        if ( paciente == null ) {
            return null;
        }
        Long id = paciente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
