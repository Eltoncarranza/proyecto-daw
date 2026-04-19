/* ============================================================
   GinecoSys — Core JS
   auth, API client, utils, navigation, toasts
   ============================================================ */

const API_URL = 'http://localhost:8080/api';

// ——— Auth state ———
let _token = localStorage.getItem('gs_token');
let _user  = JSON.parse(localStorage.getItem('gs_user') || 'null');

function getToken()  { return _token; }
function getUser()   { return _user; }
function isLoggedIn(){ return !!_token; }

function setSession(token, user) {
  _token = token;
  _user  = user;
  localStorage.setItem('gs_token', token);
  localStorage.setItem('gs_user', JSON.stringify(user));
}

function clearSession() {
  _token = null; _user = null;
  localStorage.removeItem('gs_token');
  localStorage.removeItem('gs_user');
}

// ——— HTTP client ———
async function api(path, opts = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (_token) headers['Authorization'] = 'Bearer ' + _token;
  if (opts.headers) Object.assign(headers, opts.headers);

  // If FormData, remove Content-Type so browser sets boundary
  if (opts.body instanceof FormData) delete headers['Content-Type'];

  const res = await fetch(API_URL + path, { ...opts, headers });

  if (res.status === 401) {
    clearSession();
    window.location.href = 'login.html';
    return null;
  }
  return res;
}

// ——— Navigation ———
function navigate(page) {
  window.location.href = page;
}

// ——— Toast notifications ———
function toast(msg, type = 'info', duration = 3200) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const t = document.createElement('div');
  const icons = { success: '✓', error: '✕', info: 'ℹ', warn: '⚠' };
  t.className = `toast toast-${type}`;
  t.innerHTML = `<span>${icons[type] || 'ℹ'}</span> ${msg}`;
  container.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; t.style.transform = 'translateY(8px)'; t.style.transition = '0.3s'; setTimeout(() => t.remove(), 320); }, duration);
}

// ——— Modal helpers ———
function openModal(id)  { document.getElementById(id)?.classList.add('open'); }
function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }

// Close modal on backdrop click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-backdrop')) e.target.classList.remove('open');
});

// ——— Format helpers ———
function fmtFecha(iso) {
  if (!iso) return '—';
  const d = new Date(iso + (iso.includes('T') ? '' : 'T12:00:00'));
  return d.toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric' });
}
function fmtFechaLarga(iso) {
  if (!iso) return '—';
  const d = new Date(iso + (iso.includes('T') ? '' : 'T12:00:00'));
  return d.toLocaleDateString('es-PE', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
}
function fmtFechaHora(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}
function iniciales(nombre) {
  if (!nombre) return '?';
  return nombre.split(' ').filter(Boolean).slice(0, 2).map(n => n[0]).join('').toUpperCase();
}
function today() { return new Date().toISOString().split('T')[0]; }

// ——— Sidebar nav highlight ———
function setActiveNav(viewId) {
  document.querySelectorAll('.nav-item').forEach(el => {
    el.classList.toggle('active', el.dataset.view === viewId);
  });
}

// ——— Shared: patient autocomplete ———
async function setupPatientAutocomplete(inputId, hiddenId, labelId, dropId, callback) {
  const input   = document.getElementById(inputId);
  const hidden  = document.getElementById(hiddenId);
  const labelEl = document.getElementById(labelId);
  const drop    = document.getElementById(dropId);
  if (!input) return;

  let timer;
  input.addEventListener('input', () => {
    clearTimeout(timer);
    const q = input.value.trim();
    if (q.length < 2) { drop.classList.remove('open'); return; }
    timer = setTimeout(async () => {
      const res = await api(`/pacientes?busqueda=${encodeURIComponent(q)}&tamano=6`);
      if (!res || !res.ok) return;
      const data = await res.json();
      const items = data.content || [];
      if (!items.length) { drop.classList.remove('open'); return; }
      drop.innerHTML = items.map(p => `
        <div class="autocomplete-item" data-id="${p.id}" data-nombre="${p.nombreCompleto}" data-tipo="${p.tipoPaciente}">
          <strong>${p.nombreCompleto}</strong>
          <span> · DNI ${p.dni} · ${p.tipoPaciente === 'GESTANTE' ? '🤰' : '♀'}</span>
        </div>`).join('');
      drop.classList.add('open');
    }, 280);
  });

  drop.addEventListener('mousedown', e => {
    const item = e.target.closest('.autocomplete-item');
    if (!item) return;
    e.preventDefault();
    hidden.value = item.dataset.id;
    input.value  = item.dataset.nombre;
    if (labelEl) { labelEl.textContent = '✓ ' + item.dataset.nombre; labelEl.style.display = 'block'; }
    drop.classList.remove('open');
    if (callback) callback(item.dataset.id, item.dataset.nombre, item.dataset.tipo);
  });

  document.addEventListener('click', e => {
    if (!input.contains(e.target) && !drop.contains(e.target)) drop.classList.remove('open');
  });
}

// Export to window for use in other files
window.GS = {
  api, getToken, getUser, isLoggedIn, setSession, clearSession,
  toast, openModal, closeModal,
  fmtFecha, fmtFechaLarga, fmtFechaHora, iniciales, today,
  setActiveNav, navigate, setupPatientAutocomplete
};
