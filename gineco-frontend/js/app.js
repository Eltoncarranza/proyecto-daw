/* ============================================================
   GinecoSys — App Init JS
   ============================================================ */

// ——— Guard: redirect to login if not authenticated ———
if (!GS.isLoggedIn()) {
  window.location.href = 'login.html';
}

const TITLES = {
  dashboard:  'Inicio',
  pacientes:  'Pacientes',
  detalle:    'Historial de paciente',
  consultas:  'Consultas de hoy',
  agenda:     'Agenda',
};

// ——— View navigation ———
function showView(viewId) {
  document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
  const target = document.getElementById('view-' + viewId);
  if (target) target.classList.add('active');

  GS.setActiveNav(viewId);
  document.getElementById('page-title').textContent = TITLES[viewId] || viewId;

  if (viewId === 'pacientes')  cargarPacientes();
  if (viewId === 'consultas')  cargarConsultasHoy();
  if (viewId === 'agenda')     iniciarAgenda();
  if (viewId === 'dashboard')  cargarDashboard();
}

// ——— App init ———
function initApp() {
  const user = GS.getUser();

  // Topbar date
  document.getElementById('topbar-date').textContent =
    new Date().toLocaleDateString('es-PE', { weekday:'long', day:'numeric', month:'long', year:'numeric' });

  // User info in sidebar
  if (user) {
    document.getElementById('user-name-display').textContent = user.nombreCompleto || user.username;
    document.getElementById('user-role-display').textContent = user.rol === 'DOCTOR' ? 'Médico' : user.rol;
    document.getElementById('user-avatar-initials').textContent = GS.iniciales(user.nombreCompleto || user.username);
  }

  // Initialize agenda date picker
  document.getElementById('agenda-date-picker').value = GS.today();

  // Default view
  showView('dashboard');

  // Fix nueva-paciente modal button
  document.querySelector('[onclick="openModal(\'modal-nueva-paciente\')"]')?.addEventListener('click', resetModalPaciente);
}

// ——— Dashboard ———
async function cargarDashboard() {
  // Load stats in parallel
  const [rPac, rConsultas, rCitas] = await Promise.all([
    GS.api('/pacientes?pagina=0&tamano=1'),
    GS.api('/consultas/hoy'),
    GS.api(`/citas/agenda?fecha=${GS.today()}`)
  ]);

  if (rPac?.ok) {
    const d = await rPac.json();
    document.getElementById('stat-total').textContent = d.totalElements ?? '—';
  }

  let consultasHoy = [];
  if (rConsultas?.ok) {
    consultasHoy = await rConsultas.json();
    document.getElementById('stat-consultas-hoy').textContent = consultasHoy.length;
  }

  let citasHoy = [];
  if (rCitas?.ok) {
    citasHoy = await rCitas.json();
    document.getElementById('stat-citas-hoy').textContent =
      citasHoy.filter(c => c.estado !== 'CANCELADA').length;
  }

  // Gestantes count (separate query)
  const rGest = await GS.api('/pacientes?pagina=0&tamano=1');
  if (rGest?.ok) {
    // We'll approximate — ideally API would have a gestantes count endpoint
    document.getElementById('stat-gestantes').textContent = '—';
  }

  // Dashboard mini consultas
  renderDashConsultas(consultasHoy.slice(0, 5));
  renderDashCitas(citasHoy.slice(0, 5));
}

function renderDashConsultas(consultas) {
  const cont = document.getElementById('dash-consultas');
  if (!cont) return;
  if (!consultas.length) {
    cont.innerHTML = `<p style="font-size:13px;color:var(--muted);padding:8px 0">Sin consultas registradas hoy</p>`;
    return;
  }
  cont.innerHTML = consultas.map(c => `
    <div style="display:flex;align-items:center;gap:10px;padding:9px 0;border-bottom:1px solid var(--border-light);cursor:pointer"
         onclick="abrirDetalle(${c.pacienteId})">
      <div style="width:34px;height:34px;border-radius:50%;background:var(--mauve-light);display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:600;color:var(--mauve);flex-shrink:0">
        ${GS.iniciales(c.pacienteNombre)}
      </div>
      <div style="flex:1;min-width:0">
        <div style="font-size:13.5px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${c.pacienteNombre}</div>
        <div style="font-size:12px;color:var(--muted)">${c.tipoConsulta}</div>
      </div>
      <span class="badge ${c.finalizada ? 'badge-sage' : 'badge-amber'}" style="font-size:10px">
        ${c.finalizada ? '✓' : '…'}
      </span>
    </div>`).join('');
}

function renderDashCitas(citas) {
  const cont = document.getElementById('dash-citas');
  if (!cont) return;
  if (!citas.length) {
    cont.innerHTML = `<p style="font-size:13px;color:var(--muted);padding:8px 0">Sin citas programadas hoy</p>`;
    return;
  }
  cont.innerHTML = citas.map(c => `
    <div style="display:flex;align-items:center;gap:10px;padding:9px 0;border-bottom:1px solid var(--border-light)">
      <div style="font-family:var(--font-display);font-size:16px;font-weight:500;color:var(--blue);min-width:50px">
        ${c.horaInicio}
      </div>
      <div style="flex:1">
        <div style="font-size:13.5px;font-weight:500">${c.pacienteNombre}</div>
        <div style="font-size:12px;color:var(--muted)">${c.motivo || 'Sin motivo especificado'}</div>
      </div>
      <span class="badge ${c.estado === 'CONFIRMADA' ? 'badge-sage' : 'badge-blue'}" style="font-size:10px">
        ${c.estado}
      </span>
    </div>`).join('');
}

// ——— Logout ———
function doLogout() {
  GS.clearSession();
  window.location.href = 'login.html';
}
// ——— Cambiar contraseña ———
async function cambiarPassword() {
  const actual    = document.getElementById('pass-actual').value;
  const nueva     = document.getElementById('pass-nueva').value;
  const confirmar = document.getElementById('pass-confirmar').value;

  if (!actual || !nueva) { GS.toast('Complete todos los campos', 'error'); return; }
  if (nueva !== confirmar) { GS.toast('Las contraseñas nuevas no coinciden', 'error'); return; }
  if (nueva.length < 6)   { GS.toast('La nueva contraseña debe tener al menos 6 caracteres', 'error'); return; }

  const res = await GS.api('/auth/cambiar-password', {
    method: 'POST',
    // CORRECCIÓN: Los nombres de los campos deben coincidir con GinecoDTOs.CambiarPasswordRequest
    body: JSON.stringify({
      actualPassword: actual,
      nuevoPassword: nueva
    })
  });

  if (res?.ok) {
    GS.toast('Contraseña actualizada correctamente ✓', 'success');
    GS.closeModal('modal-password');
    ['pass-actual','pass-nueva','pass-confirmar'].forEach(id => document.getElementById(id).value = '');
  } else {
    const err = res ? await res.json() : {};
    GS.toast(err.message || 'Contraseña actual incorrecta', 'error');
  }
}

// ——— Start ———
initApp();
