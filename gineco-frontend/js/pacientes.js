/* ============================================================
   GinecoSys — Pacientes JS
   ============================================================ */

let _busqTimer;
let _pacienteActual = null;
let _currentPage = 0;
let _currentTipo = '';

function debounceSearch() {
  clearTimeout(_busqTimer);
  _busqTimer = setTimeout(() => cargarPacientes(0), 350);
}

function filtrarTipo(tipo) {
  _currentTipo = tipo;
  cargarPacientes(0);
}

async function cargarPacientes(pagina = 0) {
  _currentPage = pagina;
  const q = document.getElementById('input-busqueda')?.value.trim() || '';
  let url = `/pacientes?pagina=${pagina}&tamano=18`;
  if (q) url += `&busqueda=${encodeURIComponent(q)}`;

  const cont = document.getElementById('lista-pacientes');
  cont.innerHTML = `<div class="loading-state"><div class="spinner"></div></div>`;

  const res = await GS.api(url);
  if (!res || !res.ok) { cont.innerHTML = '<div class="empty-state"><div class="es-icon">⚠️</div><h3>Error al cargar</h3><p>Verifique la conexión con el servidor</p></div>'; return; }

  const data = await res.json();
  let list = data.content || [];

  // Filtro tipo local si el backend no lo soporta en ese endpoint
  if (_currentTipo) list = list.filter(p => p.tipoPaciente === _currentTipo);

  renderPacientes(list, data);
}

function renderPacientes(pacientes, page) {
  const cont = document.getElementById('lista-pacientes');
  if (!pacientes.length) {
    cont.innerHTML = `<div class="empty-state">
      <div class="es-icon">👤</div>
      <h3>No se encontraron pacientes</h3>
      <p>Intente con otro término de búsqueda o registre una nueva paciente</p>
    </div>`;
    document.getElementById('paginacion').innerHTML = '';
    return;
  }

  cont.innerHTML = pacientes.map(p => {
    const esGest = p.tipoPaciente === 'GESTANTE';
    return `<div class="patient-item" onclick="abrirDetalle(${p.id})">
      <div class="patient-avatar ${esGest ? 'avatar-preg' : 'avatar-gine'}">
        ${GS.iniciales(p.nombreCompleto)}
      </div>
      <div class="patient-details">
        <div class="patient-name">${p.nombreCompleto}</div>
        <div class="patient-meta">
          DNI: <strong>${p.dni}</strong>
          ${p.edad ? ` · ${p.edad} años` : ''}
          ${p.telefono ? ` · 📞 ${p.telefono}` : ''}
          · ${p.totalConsultas || 0} consulta${p.totalConsultas !== 1 ? 's' : ''}
        </div>
      </div>
      <span class="badge ${esGest ? 'badge-blue' : 'badge-mauve'}">
        ${esGest ? '🤰 Gestante' : '♀ Ginecológica'}
      </span>
      <div class="patient-actions" onclick="event.stopPropagation()">
        <button class="btn btn-sm btn-secondary" onclick="abrirDetalle(${p.id})">
          Ver historial
        </button>
        <button class="btn btn-sm btn-sage" onclick="prepararConsulta(${p.id},'${escHtml(p.nombreCompleto)}',event)">
          + Consulta
        </button>
      </div>
    </div>`;
  }).join('');

  // Paginación
  const pag = document.getElementById('paginacion');
  if (page.totalPages > 1) {
    pag.innerHTML = Array.from({ length: page.totalPages }, (_, i) =>
      `<button class="btn btn-sm ${i === page.number ? 'btn-primary' : 'btn-secondary'}"
        onclick="cargarPacientes(${i})">${i + 1}</button>`
    ).join('');
  } else pag.innerHTML = '';
}

// ——— Abrir detalle ———
async function abrirDetalle(id) {
  showView('detalle');
  const cont = document.getElementById('detalle-contenido');
  cont.innerHTML = `<div class="loading-state"><div class="spinner"></div></div>`;

  const [rPac, rConsultas] = await Promise.all([
    GS.api(`/pacientes/${id}`),
    GS.api(`/consultas/paciente/${id}/historial`)
  ]);

  if (!rPac.ok) { cont.innerHTML = `<div class="empty-state"><div class="es-icon">⚠️</div><h3>Error al cargar paciente</h3></div>`; return; }

  const pac = await rPac.json();
  const consultas = rConsultas.ok ? await rConsultas.json() : [];
  _pacienteActual = pac;

  let gestante = null;
  if (pac.tipoPaciente === 'GESTANTE') {
    const rG = await GS.api(`/pacientes/${id}/gestante`);
    if (rG && rG.ok) gestante = await rG.json();
  }

  cont.innerHTML = buildDetalle(pac, consultas, gestante);
  // Active tab
  activarTab('tab-historial');
}

function buildDetalle(pac, consultas, gestante) {
  const esGest = pac.tipoPaciente === 'GESTANTE';
  return `
    <!-- Hero header -->
    <div class="patient-hero">
      <div class="patient-hero-avatar ${esGest ? 'avatar-preg' : 'avatar-gine'}" style="font-size:26px;font-weight:700">
        ${GS.iniciales(pac.nombreCompleto)}
      </div>
      <div style="flex:1">
        <div class="patient-hero-name">${pac.nombreCompleto}</div>
        <div class="patient-hero-meta">
          DNI: <strong>${pac.dni}</strong>
          ${pac.edad ? ` · ${pac.edad} años` : ''}
          ${pac.grupoSanguineo ? ` · 🩸 ${pac.grupoSanguineo}` : ''}
          ${pac.telefono ? ` · 📞 ${pac.telefono}` : ''}
        </div>
        <div style="display:flex;gap:8px;flex-wrap:wrap">
          <span class="badge ${esGest ? 'badge-blue' : 'badge-mauve'}">
            ${esGest ? '🤰 Gestante' : '♀ Ginecológica'}
          </span>
          <span class="badge badge-gray">📋 ${consultas.length} consulta${consultas.length !== 1 ? 's' : ''}</span>
          ${pac.alergias ? `<span class="badge badge-amber">⚠ Alergias</span>` : ''}
        </div>
      </div>
      <div style="display:flex;flex-direction:column;gap:8px;align-items:flex-end">
        <button class="btn btn-sage btn-sm" onclick="prepararConsulta(${pac.id},'${escHtml(pac.nombreCompleto)}')">
          + Nueva consulta
        </button>
        <button class="btn btn-secondary btn-sm" onclick="abrirEditarPaciente(${pac.id})">
          ✏ Editar datos
        </button>
        ${esGest
          ? `<button class="btn btn-sm btn-blue" onclick="abrirModalGestante(${pac.id})">
              🤰 ${gestante ? 'Ver/Editar embarazo' : 'Registrar embarazo'}
             </button>`
          : `<button class="btn btn-sm" style="background:var(--blue-light);color:var(--blue);border:1.5px solid rgba(74,111,165,0.3)"
              onclick="convertirAGestante(${pac.id})">
              🤰 Convertir a gestante
             </button>`
        }
      </div>
    </div>

    ${gestante ? buildBannerGestante(gestante) : ''}

    <!-- Tabs -->
    <div class="tab-bar">
      <button class="tab-btn active" onclick="activarTab('tab-historial',this)">
        📋 Historial (${consultas.length})
      </button>
      <button class="tab-btn" onclick="activarTab('tab-datos',this)">👤 Datos</button>
      ${gestante ? `<button class="tab-btn" onclick="activarTab('tab-embarazo',this)">🤰 Embarazo</button>` : ''}
    </div>

    <div id="tab-historial" class="tab-panel active">
      ${buildHistorial(consultas, pac)}
    </div>
    <div id="tab-datos" class="tab-panel">
      ${buildDatosPaciente(pac)}
    </div>
    ${gestante ? `<div id="tab-embarazo" class="tab-panel">${buildDatosGestante(gestante)}</div>` : ''}
  `;
}

function activarTab(tabId, btn) {
  document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  document.getElementById(tabId)?.classList.add('active');
  if (btn) btn.classList.add('active');
}

function buildBannerGestante(g) {
  const ecoDescMap = {
    ECO_TEMPRANA:         ['Ecografía de datación',   'Confirma embarazo y fecha probable de parto (< 11 semanas)'],
    ECO_PRIMER_TRIMESTRE: ['Tamizaje genético',        'Translucencia nucal, hueso nasal, marcadores cromosómicos (11–14 sem)'],
    ECO_MORFOLOGICA:      ['Morfológica estructural',  'Evaluación detallada de órganos fetales (18–24 sem)'],
    ECO_TERCER_TRIMESTRE: ['Ecografía tercer trimestre','Crecimiento fetal y bienestar (28–32 sem)'],
    ECO_FINAL:            ['Ecografía de término',     'Posición, peso estimado, madurez placentaria (≥ 36 sem)'],
    NINGUNA_URGENTE:      ['Sin urgencia ecográfica',  'Continuar controles habituales en este período']
  };
  const [ecoTitulo, ecoDesc] = ecoDescMap[g.recomendacionEcografia] || ['Control ecográfico',''];
  const fppFmt = GS.fmtFecha(g.fechaProbableParto);

  return `
    <div class="preg-banner">
      <div class="weeks-dial">
        <div class="num">${g.semanasActuales}</div>
        <div class="lbl">semanas</div>
      </div>
      <div style="flex:1">
        <div style="font-weight:600;font-size:15px;color:var(--blue);margin-bottom:4px">
          Embarazo en curso
          ${g.embarazoAltoRiesgo ? ' <span class="badge badge-red" style="margin-left:6px">⚠ Alto riesgo</span>' : ''}
        </div>
        <div style="font-size:13px;color:var(--muted)">
          FUR: ${GS.fmtFecha(g.fechaUltimaRegla)}
          · FPP estimada: <strong style="color:var(--ink)">${fppFmt}</strong>
        </div>
        ${g.grupoSanguineo ? `<div style="font-size:12.5px;color:var(--muted);margin-top:3px">Grupo: ${g.grupoSanguineo} / ${g.rhNegativo ? 'Rh(−)' : 'Rh(+)'} · G${g.gestaciones||0}P${g.partos||0}C${g.cesareas||0}A${g.abortos||0}</div>` : ''}
      </div>
    </div>
    <div class="eco-alert">
      <div class="eco-alert-icon">🔬</div>
      <div>
        <h4>Recomendación: ${ecoTitulo}</h4>
        <p>${ecoDesc}</p>
      </div>
    </div>
  `;
}

function buildHistorial(consultas, pac) {
  if (!consultas.length) return `
    <div class="empty-state">
      <div class="es-icon">📋</div>
      <h3>Sin consultas registradas</h3>
      <p>Use el botón "Nueva consulta" para registrar la primera atención de esta paciente</p>
    </div>`;

  return consultas.map(c => {
    const d = new Date(c.fechaConsulta);
    const day = d.toLocaleDateString('es-PE', { day: '2-digit' });
    const mon = d.toLocaleDateString('es-PE', { month: 'short' }).replace('.', '');
    const hora = d.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });

    return `<div class="consulta-card">
      <div class="consulta-card-header" onclick="toggleConsulta(${c.id}, this)">
        <div class="consulta-date-badge">
          <div class="day">${day}</div>
          <div class="month">${mon}</div>
        </div>
        <div style="flex:1">
          <div style="font-weight:500;font-size:14.5px;margin-bottom:3px">
            ${c.motivoConsulta || c.tipoConsulta}
          </div>
          <div style="font-size:12.5px;color:var(--muted)">
            ${hora} · ${c.doctorNombre}
          </div>
        </div>
        <div style="display:flex;gap:8px;align-items:center">
          <span class="badge ${c.tipoConsulta === 'OBSTETRICA' ? 'badge-blue' : 'badge-mauve'}">${c.tipoConsulta}</span>
          ${c.finalizada
            ? '<span class="badge badge-sage">✓ Finalizada</span>'
            : '<span class="badge badge-amber">En proceso</span>'}
          <button class="btn btn-xs btn-secondary" onclick="event.stopPropagation();editarConsulta(${c.id})">✏ Editar</button>
          <span style="color:var(--muted);font-size:12px">▾</span>
        </div>
      </div>
      <div class="consulta-card-body" id="cb-${c.id}">
        ${buildCuerpoConsulta(c, pac)}
      </div>
    </div>`;
  }).join('');
}

function buildCuerpoConsulta(c, pac) {
  const vitales = [
    c.presionArterial && ['🩺', 'PA', c.presionArterial],
    c.temperatura    && ['🌡', 'Temp', `${c.temperatura}°C`],
    c.frecuenciaCardiaca && ['❤️', 'FC', `${c.frecuenciaCardiaca} lpm`],
    c.peso           && ['⚖', 'Peso', `${c.peso} kg`],
    c.fcfBebe        && ['👶', 'FCF', c.fcfBebe],
    c.alturaUterina  && ['📏', 'AU', c.alturaUterina],
    c.hemoglobinaActual && ['🔴', 'Hb', c.hemoglobinaActual],
  ].filter(Boolean);

  return `
    ${vitales.length ? `<div class="vitals-strip">
      ${vitales.map((v, i) => `
        ${i > 0 ? '<div class="vital-sep"></div>' : ''}
        <div class="vital-item">
          <div class="vital-value">${v[2]}</div>
          <div class="vital-label">${v[1]}</div>
        </div>`).join('')}
    </div>` : ''}

    ${campo('Anamnesis', c.anamnesis)}
    ${campo('Examen físico', c.examenFisico)}
    ${campo('Diagnóstico', c.diagnostico)}
    ${campo('Tratamiento', c.tratamiento)}
    ${campo('Indicaciones', c.indicaciones)}
    ${campo('Resultados de laboratorio', c.resultadosLaboratorio)}
    ${campo('Notas adicionales', c.notasAdicionales)}

    ${c.archivos && c.archivos.length ? `
      <div class="field-group">
        <div class="field-label">Archivos adjuntos (${c.archivos.length})</div>
        <div class="files-grid">
          ${c.archivos.map(a => `
            <div class="file-thumb" onclick="window.open('${a.urlArchivo}','_blank')">
              <div class="fi">${a.tipoArchivo === 'PDF' || a.tipoArchivo === 'LABORATORIO' ? '📄' : '🖼️'}</div>
              <div class="fn">${a.nombreOriginal}</div>
              <div class="ft">${a.tipoArchivo}</div>
            </div>`).join('')}
        </div>
      </div>` : ''}

    <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:16px;padding-top:14px;border-top:1px solid var(--border-light)">
      <label class="btn btn-sm btn-secondary" style="cursor:pointer">
        📎 Adjuntar archivo
        <input type="file" accept="image/*,.pdf" style="display:none"
          onchange="subirArchivo(${c.id},this)">
      </label>
      ${!c.finalizada ? `<button class="btn btn-sm btn-sage" onclick="finalizarConsulta(${c.id})">✅ Finalizar</button>` : ''}
      <button class="btn btn-sm btn-blue" onclick="agendarCitaDesdePaciente(${pac ? pac.id : 0},'${pac ? escHtml(pac.nombreCompleto) : ''}')">
        📅 Agendar cita
      </button>
    </div>`;
}

function campo(label, valor) {
  if (!valor) return '';
  return `<div class="field-group">
    <div class="field-label">${label}</div>
    <div class="field-value">${escHtml(valor)}</div>
  </div>`;
}

function toggleConsulta(id, header) {
  const body = document.getElementById(`cb-${id}`);
  const isOpen = body.classList.toggle('open');
  header.classList.toggle('expanded', isOpen);
}

function buildDatosPaciente(p) {
  return `<div class="card">
    <div class="info-grid">
      ${ii('DNI', p.dni)}
      ${ii('Fecha de nacimiento', GS.fmtFecha(p.fechaNacimiento))}
      ${ii('Edad', p.edad ? `${p.edad} años` : '—')}
      ${ii('Grupo sanguíneo', p.grupoSanguineo || '—')}
      ${ii('Teléfono', p.telefono || '—')}
      ${ii('Email', p.email || '—')}
    </div>
    ${p.alergias ? `<div style="margin-top:16px"><div class="field-label">Alergias</div><div class="field-value">${escHtml(p.alergias)}</div></div>` : ''}
    ${p.antecedentesPersonales ? `<div style="margin-top:12px"><div class="field-label">Antecedentes personales</div><div class="field-value">${escHtml(p.antecedentesPersonales)}</div></div>` : ''}
    ${p.antecedentesFamiliares ? `<div style="margin-top:12px"><div class="field-label">Antecedentes familiares</div><div class="field-value">${escHtml(p.antecedentesFamiliares)}</div></div>` : ''}
    ${p.contactoEmergenciaNombre ? `
      <div style="margin-top:16px;padding:12px 14px;background:var(--amber-light);border-radius:var(--r-md);border:1px solid rgba(196,135,74,0.25)">
        <div class="field-label" style="color:var(--amber)">Contacto de emergencia</div>
        <div class="field-value">${escHtml(p.contactoEmergenciaNombre)}
          ${p.contactoEmergenciaRelacion ? ` (${p.contactoEmergenciaRelacion})` : ''}
          ${p.contactoEmergenciaTelefono ? ` · 📞 ${p.contactoEmergenciaTelefono}` : ''}
        </div>
      </div>` : ''}
    <div style="margin-top:16px">
      <button class="btn btn-secondary btn-sm" onclick="abrirEditarPaciente(${p.id})">✏ Editar datos del paciente</button>
    </div>
  </div>`;
}

function buildDatosGestante(g) {
  return `<div class="card">
    <div class="info-grid">
      ${ii('FUR', GS.fmtFecha(g.fechaUltimaRegla))}
      ${ii('FPP estimada', GS.fmtFecha(g.fechaProbableParto))}
      ${ii('Semanas actuales', `${g.semanasActuales} semanas`)}
      ${ii('Grupo / Factor Rh', `${g.grupoSanguineo || '—'} / ${g.rhNegativo ? 'Rh(−)' : 'Rh(+)'}`)}
      ${ii('Historia obstétrica', `G${g.gestaciones||0} P${g.partos||0} C${g.cesareas||0} A${g.abortos||0}`)}
      ${ii('Peso inicial', g.pesoInicial ? `${g.pesoInicial} kg` : '—')}
      ${ii('Talla', g.talla ? `${g.talla} cm` : '—')}
      ${ii('Hemoglobina inicial', g.hemoglobinaInicial || '—')}
      ${ii('Alto riesgo', g.embarazoAltoRiesgo ? '⚠ SÍ' : 'No')}
    </div>
    ${g.factoresRiesgo ? `<div style="margin-top:14px"><div class="field-label">Factores de riesgo</div><div class="field-value">${escHtml(g.factoresRiesgo)}</div></div>` : ''}
    ${g.notasGenerales ? `<div style="margin-top:12px"><div class="field-label">Notas generales</div><div class="field-value">${escHtml(g.notasGenerales)}</div></div>` : ''}
    <div style="margin-top:16px">
      <button class="btn btn-blue btn-sm" onclick="abrirModalGestante(${g.pacienteId})">✏ Editar datos del embarazo</button>
    </div>
  </div>`;
}

function ii(label, val) {
  return `<div class="info-item"><div class="lbl">${label}</div><div class="val">${val || '—'}</div></div>`;
}

// ——— Guardar paciente ———
async function guardarPaciente() {
  const editId = document.getElementById('edit-paciente-id').value;
  const dni      = document.getElementById('nv-dni').value.trim();
  const nombres  = document.getElementById('nv-nombres').value.trim();
  const apellidos = document.getElementById('nv-apellidos').value.trim();
  const fechaNac = document.getElementById('nv-fechanac').value;

  if (!dni || !nombres || !apellidos || !fechaNac) {
    GS.toast('Complete los campos obligatorios: DNI, nombres, apellidos y fecha de nacimiento', 'error'); return;
  }

  const body = {
    dni, nombres, apellidos, fechaNacimiento: fechaNac,
    tipoPaciente: document.getElementById('nv-tipo').value,
    grupoSanguineo: document.getElementById('nv-grupo').value || null,
    telefono: document.getElementById('nv-tel').value || null,
    email: document.getElementById('nv-email').value || null,
    direccion: document.getElementById('nv-dir').value || null,
    alergias: document.getElementById('nv-alerg').value || null,
    antecedentesPersonales: document.getElementById('nv-antec-pers').value || null,
    antecedentesFamiliares: document.getElementById('nv-antec-fam').value || null,
    contactoEmergenciaNombre: document.getElementById('nv-emer-nombre').value || null,
    contactoEmergenciaTelefono: document.getElementById('nv-emer-tel').value || null,
    contactoEmergenciaRelacion: document.getElementById('nv-emer-rel').value || null,
  };

  const method = editId ? 'PUT' : 'POST';
  const url = editId ? `/pacientes/${editId}` : '/pacientes';
  const res = await GS.api(url, { method, body: JSON.stringify(body) });

  if (!res || !res.ok) {
    const err = res ? await res.json() : {};
    GS.toast(err.message || 'Error al guardar la paciente', 'error'); return;
  }

  const pac = await res.json();
  GS.toast(editId ? 'Datos actualizados correctamente' : 'Paciente registrada correctamente', 'success');
  GS.closeModal('modal-nueva-paciente');

  if (!editId && body.tipoPaciente === 'GESTANTE') {
    _pacienteActual = pac;
    GS.openModal('modal-gestante');
  } else {
    cargarPacientes(_currentPage);
    if (editId) abrirDetalle(editId);
  }
}

// ——— Editar paciente ———
async function abrirEditarPaciente(id) {
  const res = await GS.api(`/pacientes/${id}`);
  if (!res || !res.ok) { GS.toast('Error al cargar datos', 'error'); return; }
  const p = await res.json();

  document.getElementById('edit-paciente-id').value = p.id;
  document.getElementById('modal-paciente-titulo').textContent = 'Editar paciente';
  document.getElementById('nv-dni').value = p.dni || '';
  document.getElementById('nv-nombres').value = p.nombres || '';
  document.getElementById('nv-apellidos').value = p.apellidos || '';
  document.getElementById('nv-fechanac').value = p.fechaNacimiento || '';
  document.getElementById('nv-tipo').value = p.tipoPaciente || 'GINECOLOGICA';
  document.getElementById('nv-grupo').value = p.grupoSanguineo || '';
  document.getElementById('nv-tel').value = p.telefono || '';
  document.getElementById('nv-email').value = p.email || '';
  document.getElementById('nv-dir').value = p.direccion || '';
  document.getElementById('nv-alerg').value = p.alergias || '';
  document.getElementById('nv-antec-pers').value = p.antecedentesPersonales || '';
  document.getElementById('nv-antec-fam').value = p.antecedentesFamiliares || '';
  document.getElementById('nv-emer-nombre').value = p.contactoEmergenciaNombre || '';
  document.getElementById('nv-emer-tel').value = p.contactoEmergenciaTelefono || '';
  document.getElementById('nv-emer-rel').value = p.contactoEmergenciaRelacion || '';

  GS.openModal('modal-nueva-paciente');
}

// ——— Gestante ———
function abrirModalGestante(pacienteId) {
  if (!_pacienteActual || _pacienteActual.id !== pacienteId) _pacienteActual = { id: pacienteId };
  document.getElementById('modal-gestante-subtitle').textContent = 'Paciente: ' + (_pacienteActual.nombreCompleto || '');
  GS.openModal('modal-gestante');
}

function calcFPP() {
  const fur = document.getElementById('gest-fur').value;
  if (!fur) return;
  const d = new Date(fur + 'T12:00:00');
  d.setDate(d.getDate() + 280);
  document.getElementById('gest-fpp').value = d.toISOString().split('T')[0];
  // Preview semanas
  const hoy = new Date();
  const dias = Math.floor((hoy - new Date(fur + 'T12:00:00')) / 86400000);
  const sem = Math.floor(dias / 7);
  const prev = document.getElementById('semanas-preview');
  if (sem >= 0 && sem <= 45) {
    prev.textContent = `Semanas aproximadas desde FUR: ${sem} semanas`;
    prev.style.display = 'block';
  }
}

async function convertirAGestante(id) {
  if (!confirm('¿Convertir esta paciente al estado gestante? Podrá registrar datos del embarazo.')) return;
  const res = await GS.api(`/pacientes/${id}/tipo`, { method: 'PATCH', body: JSON.stringify({ tipo: 'GESTANTE' }) });
  if (res && res.ok) {
    GS.toast('Paciente convertida a gestante ✓', 'success');
    abrirDetalle(id);
  }
}

async function guardarGestante() {
  if (!_pacienteActual) { GS.toast('Error: paciente no identificada', 'error'); return; }
  const fur = document.getElementById('gest-fur').value;
  if (!fur) { GS.toast('La fecha de última regla es obligatoria', 'error'); return; }

  const body = {
    fechaUltimaRegla: fur,
    fechaUltimaEcografia: document.getElementById('gest-fecha-eco').value || null,
    semanasEcografia: document.getElementById('gest-sem-eco').value ? +document.getElementById('gest-sem-eco').value : null,
    fechaProbableParto: document.getElementById('gest-fpp').value || null,
    gestaciones: +document.getElementById('gest-g').value || 0,
    partos:      +document.getElementById('gest-p').value || 0,
    cesareas:    +document.getElementById('gest-c').value || 0,
    abortos:     +document.getElementById('gest-a').value || 0,
    grupoSanguineo: document.getElementById('gest-grupo').value || null,
    rhNegativo: document.getElementById('gest-rh').value === 'true',
    pesoInicial: document.getElementById('gest-peso').value ? +document.getElementById('gest-peso').value : null,
    talla:       document.getElementById('gest-talla').value ? +document.getElementById('gest-talla').value : null,
    factoresRiesgo: document.getElementById('gest-riesgo').value || null,
    embarazoAltoRiesgo: document.getElementById('gest-alto-riesgo').value === 'true',
    hemoglobinaInicial: document.getElementById('gest-hb').value || null,
    hematocritoInicial: document.getElementById('gest-hcto').value || null,
    notasGenerales: document.getElementById('gest-notas').value || null,
  };

  const pid = _pacienteActual.id;
  let res = await GS.api(`/pacientes/${pid}/gestante`, { method: 'POST', body: JSON.stringify(body) });
  if (res && res.status === 400) res = await GS.api(`/pacientes/${pid}/gestante`, { method: 'PUT', body: JSON.stringify(body) });

  if (res && res.ok) {
    GS.toast('Datos del embarazo guardados ✓', 'success');
    GS.closeModal('modal-gestante');
    abrirDetalle(pid);
  } else {
    const err = res ? await res.json() : {};
    GS.toast(err.message || 'Error al guardar', 'error');
  }
}

// ——— Archivo upload ———
async function subirArchivo(consultaId, input) {
  const file = input.files[0];
  if (!file) return;
  const tipo = file.type === 'application/pdf' ? 'LABORATORIO' : 'ECOGRAFIA';
  const fd = new FormData();
  fd.append('archivo', file);
  fd.append('tipo', tipo);

  const res = await GS.api(`/consultas/${consultaId}/archivos`, { method: 'POST', body: fd });
  if (res && res.ok) {
    GS.toast('Archivo adjuntado ✓', 'success');
    if (_pacienteActual) abrirDetalle(_pacienteActual.id);
  } else GS.toast('Error al subir archivo', 'error');
}

// ——— utils ———
function escHtml(str) {
  if (!str) return '';
  return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// Reset modal al abrir nueva paciente
function resetModalPaciente() {
  document.getElementById('edit-paciente-id').value = '';
  document.getElementById('modal-paciente-titulo').textContent = 'Registrar nueva paciente';
  ['nv-dni','nv-nombres','nv-apellidos','nv-fechanac','nv-tel','nv-email','nv-dir',
   'nv-alerg','nv-antec-pers','nv-antec-fam','nv-emer-nombre','nv-emer-tel','nv-emer-rel'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = '';
  });
  document.getElementById('nv-grupo').value = '';
  document.getElementById('nv-tipo').value = 'GINECOLOGICA';
}

// Override openModal for paciente modal to reset
const _origOpenModal = window.GS?.openModal;
document.getElementById('modal-nueva-paciente')?.addEventListener('click', () => {});
