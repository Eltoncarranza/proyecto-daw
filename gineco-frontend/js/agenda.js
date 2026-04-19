/* ============================================================
   GinecoSys — Agenda JS
   ============================================================ */

let _agendaFecha = new Date().toISOString().split('T')[0];

function iniciarAgenda() {
  const picker = document.getElementById('agenda-date-picker');
  if (picker) picker.value = _agendaFecha;
  actualizarLabelAgenda();
  cargarAgenda();
}

function actualizarLabelAgenda() {
  const el = document.getElementById('agenda-fecha-label');
  if (!el) return;
  const d = new Date(_agendaFecha + 'T12:00:00');
  el.textContent = d.toLocaleDateString('es-PE', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
  });
}

function cambiarDiaAgenda(delta) {
  const d = new Date(_agendaFecha + 'T12:00:00');
  d.setDate(d.getDate() + delta);
  _agendaFecha = d.toISOString().split('T')[0];
  const picker = document.getElementById('agenda-date-picker');
  if (picker) picker.value = _agendaFecha;
  actualizarLabelAgenda();
  cargarAgenda();
}

function agendaFechaManual() {
  _agendaFecha = document.getElementById('agenda-date-picker').value;
  actualizarLabelAgenda();
  cargarAgenda();
}

async function cargarAgenda() {
  const cont = document.getElementById('agenda-lista');
  if (!cont) return;
  cont.innerHTML = `<div class="loading-state"><div class="spinner"></div></div>`;

  const res = await GS.api(`/citas/agenda?fecha=${_agendaFecha}`);
  if (!res || !res.ok) {
    cont.innerHTML = `<div class="empty-state"><div class="es-icon">⚠️</div><h3>Error al cargar agenda</h3></div>`;
    return;
  }

  const citas = await res.json();
  if (!citas.length) {
    cont.innerHTML = `<div class="empty-state">
      <div class="es-icon">📅</div>
      <h3>Sin citas este día</h3>
      <p>Use el botón "Nueva cita" para programar una atención</p>
    </div>`;
    // Update dashboard counter
    const el = document.getElementById('stat-citas-hoy');
    if (el && _agendaFecha === GS.today()) el.textContent = '0';
    return;
  }

  const estadoClase = {
    PROGRAMADA: 'badge-blue', CONFIRMADA: 'badge-sage',
    ATENDIDA: 'badge-sage', CANCELADA: 'badge-red', NO_ASISTIO: 'badge-red'
  };

  cont.innerHTML = citas.map(c => `
    <div class="agenda-slot ${c.estado === 'CANCELADA' ? 'cancelada' : ''}">
      <div class="agenda-time">${c.horaInicio}</div>
      <div class="agenda-slot-info">
        <div class="agenda-paciente">${c.pacienteNombre}</div>
        <div class="agenda-detail">
          DNI: ${c.pacienteDni} · Hasta: ${c.horaFin}
          ${c.motivo ? ` · ${c.motivo}` : ''}
        </div>
      </div>
      <span class="badge ${estadoClase[c.estado] || 'badge-gray'}">${c.estado}</span>
      <div style="display:flex;gap:6px">
        ${c.estado !== 'CANCELADA' && c.estado !== 'ATENDIDA' ?
          `<button class="btn btn-sm btn-sage"
            onclick="prepararConsulta(${c.pacienteId},'${c.pacienteNombre.replace(/'/g,"\\'")}')">
            📋 Atender
           </button>` : ''}
        ${c.estado === 'PROGRAMADA' ?
          `<button class="btn btn-sm btn-secondary"
            onclick="confirmarCita(${c.id})">Confirmar</button>` : ''}
        ${c.estado !== 'CANCELADA' ?
          `<button class="btn btn-xs btn-danger" onclick="cancelarCita(${c.id})">✕</button>` : ''}
      </div>
    </div>
  `).join('');

  // Update dashboard
  const el = document.getElementById('stat-citas-hoy');
  if (el && _agendaFecha === GS.today()) el.textContent = citas.filter(c => c.estado !== 'CANCELADA').length;
}

// ——— Calcular duración de la cita ———
function calcDuracionCita() {
  const ini = document.getElementById('cita-inicio').value;
  const fin = document.getElementById('cita-fin').value;
  const el  = document.getElementById('cita-duracion');
  const warn = document.getElementById('cita-conflicto');
  warn.style.display = 'none';
  if (!ini || !fin) { el.textContent = ''; return; }
  const [h1,m1] = ini.split(':').map(Number);
  const [h2,m2] = fin.split(':').map(Number);
  const mins = (h2*60+m2) - (h1*60+m1);
  if (mins <= 0) { el.textContent = '⚠ La hora de fin debe ser posterior a la hora de inicio'; el.style.color = 'var(--red)'; return; }
  el.textContent = `⏱ Duración: ${mins} minutos`;
  el.style.color = mins >= 30 ? 'var(--sage)' : 'var(--amber)';
  if (mins < 30) el.textContent += ' — mínimo requerido: 30 min';
}

// ——— Guardar cita ———
async function guardarCita() {
  const pacId  = document.getElementById('cita-pac-id').value;
  const fecha  = document.getElementById('cita-fecha').value;
  const inicio = document.getElementById('cita-inicio').value;
  const fin    = document.getElementById('cita-fin').value;

  if (!pacId || !fecha || !inicio || !fin) {
    GS.toast('Complete todos los campos obligatorios', 'error'); return;
  }

  const body = {
    pacienteId: +pacId,
    fecha, horaInicio: inicio, horaFin: fin,
    motivo: document.getElementById('cita-motivo').value || null,
    notas:  document.getElementById('cita-notas').value || null,
  };

  const res = await GS.api('/citas', { method: 'POST', body: JSON.stringify(body) });
  if (!res || !res.ok) {
    const err = res ? await res.json() : {};
    const warn = document.getElementById('cita-conflicto');
    warn.textContent = '⚠ ' + (err.message || 'Conflicto de horario detectado');
    warn.style.display = 'block';
    return;
  }

  GS.toast('Cita programada correctamente ✓', 'success');
  GS.closeModal('modal-nueva-cita');
  _agendaFecha = fecha;
  const picker = document.getElementById('agenda-date-picker');
  if (picker) picker.value = fecha;
  actualizarLabelAgenda();
  cargarAgenda();
}

async function confirmarCita(id) {
  const res = await GS.api(`/citas/${id}/estado`, {
    method: 'PATCH', body: JSON.stringify({ estado: 'CONFIRMADA' })
  });
  if (res && res.ok) { GS.toast('Cita confirmada ✓', 'success'); cargarAgenda(); }
}

async function cancelarCita(id) {
  if (!confirm('¿Desea cancelar esta cita?')) return;
  const res = await GS.api(`/citas/${id}`, { method: 'DELETE' });
  if (res && res.ok) { GS.toast('Cita cancelada', 'info'); cargarAgenda(); }
}

function agendarCitaDesdePaciente(pacId, pacNombre) {
  document.getElementById('cita-pac-id').value = pacId;
  document.getElementById('cita-busq-pac').value = pacNombre;
  document.getElementById('cita-pac-sel').textContent = '✓ ' + pacNombre;
  document.getElementById('cita-pac-sel').style.display = 'block';
  document.getElementById('cita-fecha').value = GS.today();
  GS.openModal('modal-nueva-cita');
}

// Setup autocomplete for cita modal
GS.setupPatientAutocomplete('cita-busq-pac', 'cita-pac-id', 'cita-pac-sel', 'cita-drop-pac');
