/* ============================================================
   GinecoSys — Consultas JS
   ============================================================ */

function abrirNuevaConsulta() {
  limpiarFormConsulta();
  document.getElementById('modal-consulta-titulo').textContent = 'Nueva consulta';
  document.getElementById('nc-selector-paciente').style.display = 'block';
  document.getElementById('modal-consulta-paciente-label').textContent = '';
  GS.openModal('modal-consulta');
}

function prepararConsulta(pacId, pacNombre, ev) {
  if (ev) ev.stopPropagation();
  limpiarFormConsulta();
  document.getElementById('nc-consulta-id').value = '';
  document.getElementById('nc-paciente-id').value = pacId;
  document.getElementById('modal-consulta-titulo').textContent = 'Nueva consulta';
  document.getElementById('modal-consulta-paciente-label').textContent = '✓ ' + pacNombre;
  document.getElementById('nc-selector-paciente').style.display = 'none';
  GS.openModal('modal-consulta');
}

async function editarConsulta(id) {
  const res = await GS.api(`/consultas/${id}`);
  if (!res || !res.ok) { GS.toast('Error al cargar consulta', 'error'); return; }
  const c = await res.json();

  limpiarFormConsulta();
  document.getElementById('nc-consulta-id').value = c.id;
  document.getElementById('nc-paciente-id').value = c.pacienteId;
  document.getElementById('modal-consulta-titulo').textContent = 'Editar consulta';
  document.getElementById('modal-consulta-paciente-label').textContent = '✓ ' + c.pacienteNombre;
  document.getElementById('nc-selector-paciente').style.display = 'none';

  // Rellenar campos
  document.getElementById('nc-tipo').value = c.tipoConsulta || 'GINECOLOGICA';
  document.getElementById('nc-motivo').value = c.motivoConsulta || '';
  document.getElementById('nc-pa').value = c.presionArterial || '';
  document.getElementById('nc-temp').value = c.temperatura || '';
  document.getElementById('nc-fc').value = c.frecuenciaCardiaca || '';
  document.getElementById('nc-peso').value = c.peso || '';
  document.getElementById('nc-fcf').value = c.fcfBebe || '';
  document.getElementById('nc-au').value = c.alturaUterina || '';
  document.getElementById('nc-hb-act').value = c.hemoglobinaActual || '';
  document.getElementById('nc-anamnesis').value = c.anamnesis || '';
  document.getElementById('nc-examen').value = c.examenFisico || '';
  document.getElementById('nc-diagnostico').value = c.diagnostico || '';
  document.getElementById('nc-tratamiento').value = c.tratamiento || '';
  document.getElementById('nc-indicaciones').value = c.indicaciones || '';
  document.getElementById('nc-lab').value = c.resultadosLaboratorio || '';
  document.getElementById('nc-notas').value = c.notasAdicionales || '';

  // Mostrar campos obstétricos si aplica
  if (c.tipoConsulta === 'OBSTETRICA' || c.fcfBebe || c.alturaUterina) {
    document.getElementById('nc-obst-fields').style.display = 'block';
  }

  GS.openModal('modal-consulta');
}

function limpiarFormConsulta() {
  ['nc-motivo','nc-pa','nc-temp','nc-fc','nc-peso','nc-fcf','nc-au',
   'nc-hb-act','nc-anamnesis','nc-examen','nc-diagnostico',
   'nc-tratamiento','nc-indicaciones','nc-lab','nc-notas'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = '';
  });
  document.getElementById('nc-tipo').value = 'GINECOLOGICA';
  document.getElementById('nc-obst-fields').style.display = 'none';
  document.getElementById('nc-busq-pac').value = '';
  document.getElementById('nc-pac-sel').style.display = 'none';
}

// Mostrar campos obstétricos según tipo
document.addEventListener('change', e => {
  if (e.target.id === 'nc-tipo') {
    const obsFields = document.getElementById('nc-obst-fields');
    if (e.target.value === 'OBSTETRICA') obsFields.style.display = 'block';
  }
});

async function guardarConsulta(finalizar) {
  const consultaId = document.getElementById('nc-consulta-id').value;
  const pacienteId = document.getElementById('nc-paciente-id').value;

  if (!pacienteId) { GS.toast('Seleccione una paciente', 'error'); return; }

  const body = {
    pacienteId: +pacienteId,
    tipoConsulta:         document.getElementById('nc-tipo').value,
    motivoConsulta:       document.getElementById('nc-motivo').value || null,
    presionArterial:      document.getElementById('nc-pa').value || null,
    temperatura:          document.getElementById('nc-temp').value ? +document.getElementById('nc-temp').value : null,
    frecuenciaCardiaca:   document.getElementById('nc-fc').value ? +document.getElementById('nc-fc').value : null,
    peso:                 document.getElementById('nc-peso').value ? +document.getElementById('nc-peso').value : null,
    fcfBebe:              document.getElementById('nc-fcf').value || null,
    alturaUterina:        document.getElementById('nc-au').value || null,
    hemoglobinaActual:    document.getElementById('nc-hb-act').value || null,
    anamnesis:            document.getElementById('nc-anamnesis').value || null,
    examenFisico:         document.getElementById('nc-examen').value || null,
    diagnostico:          document.getElementById('nc-diagnostico').value || null,
    tratamiento:          document.getElementById('nc-tratamiento').value || null,
    indicaciones:         document.getElementById('nc-indicaciones').value || null,
    resultadosLaboratorio: document.getElementById('nc-lab').value || null,
    notasAdicionales:     document.getElementById('nc-notas').value || null,
  };

  const method = consultaId ? 'PUT' : 'POST';
  const url    = consultaId ? `/consultas/${consultaId}` : '/consultas';
  const res = await GS.api(url, { method, body: JSON.stringify(body) });

  if (!res || !res.ok) {
    const err = res ? await res.json() : {};
    GS.toast(err.message || 'Error al guardar consulta', 'error'); return;
  }

  const consulta = await res.json();

  if (finalizar) {
    await GS.api(`/consultas/${consulta.id}/finalizar`, { method: 'PATCH' });
  }

  GS.toast(finalizar ? 'Consulta finalizada ✓' : 'Consulta guardada ✓', 'success');
  GS.closeModal('modal-consulta');

  if (_pacienteActual && _pacienteActual.id == pacienteId) {
    abrirDetalle(pacienteId);
  } else {
    cargarConsultasHoy();
  }
}

async function finalizarConsulta(id) {
  const res = await GS.api(`/consultas/${id}/finalizar`, { method: 'PATCH' });
  if (res && res.ok) {
    GS.toast('Consulta finalizada ✓', 'success');
    if (_pacienteActual) abrirDetalle(_pacienteActual.id);
    else cargarConsultasHoy();
  }
}

// ——— Consultas hoy ———
async function cargarConsultasHoy() {
  const label = document.getElementById('consultas-fecha-label');
  if (label) label.textContent = GS.fmtFechaLarga(new Date().toISOString());

  const cont = document.getElementById('consultas-hoy-lista');
  cont.innerHTML = `<div class="loading-state"><div class="spinner"></div></div>`;

  const res = await GS.api('/consultas/hoy');
  if (!res || !res.ok) {
    cont.innerHTML = `<div class="empty-state"><div class="es-icon">⚠️</div><h3>Error al cargar consultas</h3></div>`;
    return;
  }

  const data = await res.json();
  if (!data.length) {
    cont.innerHTML = `<div class="empty-state">
      <div class="es-icon">📋</div>
      <h3>Sin consultas hoy</h3>
      <p>Use el botón "Nueva consulta" para registrar la primera atención del día</p>
    </div>`;
    return;
  }

  cont.innerHTML = `<div class="patient-list">
    ${data.map(c => `
      <div class="patient-item" onclick="abrirDetalle(${c.pacienteId})">
        <div class="patient-avatar avatar-gine">${GS.iniciales(c.pacienteNombre)}</div>
        <div class="patient-details">
          <div class="patient-name">${c.pacienteNombre}</div>
          <div class="patient-meta">
            ${GS.fmtFechaHora(c.fechaConsulta)} · ${c.tipoConsulta}
            ${c.motivoConsulta ? ` · ${c.motivoConsulta}` : ''}
          </div>
        </div>
        <span class="badge ${c.finalizada ? 'badge-sage' : 'badge-amber'}">
          ${c.finalizada ? '✓ Finalizada' : 'En proceso'}
        </span>
        <div class="patient-actions" onclick="event.stopPropagation()">
          <button class="btn btn-sm btn-secondary" onclick="editarConsulta(${c.id})">✏ Editar</button>
          <button class="btn btn-sm btn-sage" onclick="abrirDetalle(${c.pacienteId})">Ver historial</button>
        </div>
      </div>`).join('')}
  </div>`;

  // Update dashboard counter
  const el = document.getElementById('stat-consultas-hoy');
  if (el) el.textContent = data.length;
}

// Setup autocomplete for consulta modal
GS.setupPatientAutocomplete('nc-busq-pac', 'nc-paciente-id', 'nc-pac-sel', 'nc-drop-pac', (id, nombre, tipo) => {
  document.getElementById('modal-consulta-paciente-label').textContent = '✓ ' + nombre;
  if (tipo === 'GESTANTE') document.getElementById('nc-obst-fields').style.display = 'block';
});
