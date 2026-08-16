'use strict';

const COLS = 40;
const ROWS = 12;
// Version del formato dentro del propio blob: sin esto no se pueden migrar niveles viejos.
const FORMAT = 1;

const state = {
  token: null,
  userId: null,
  role: null,
  level: null,
  objects: new Map(),
  tool: 'block',
  painting: false,
};

const $ = (id) => document.getElementById(id);

// --- transporte --------------------------------------------------------------

function log(ok, text) {
  const line = document.createElement('div');
  line.className = ok ? 'ok' : 'ko';
  line.textContent = text;
  $('log').prepend(line);
}

async function api(path, { method = 'GET', body, auth = false, raw = false } = {}) {
  const headers = { 'Accept-Language': 'es' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (auth && state.token) headers['Authorization'] = `Bearer ${state.token}`;

  const res = await fetch(path, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) });

  if (raw) {
    if (!res.ok) {
      log(false, `${method} ${path} → ${res.status}`);
      throw new Error(String(res.status));
    }
    log(true, `${method} ${path} → ${res.status} (binario)`);
    return res.arrayBuffer();
  }

  let payload = null;
  try {
    payload = await res.json();
  } catch {
    payload = null;
  }

  if (!res.ok) {
    const code = payload?.error?.code ?? res.status;
    const message = payload?.message ?? res.statusText;
    log(false, `${method} ${path} → ${res.status} ${code}\n${message}`);
    throw new Error(message);
  }

  log(true, `${method} ${path} → ${res.status}\n${payload?.message ?? ''}`);
  return payload?.data;
}

// --- sesion ------------------------------------------------------------------

function claimsOf(token) {
  const part = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
  return JSON.parse(atob(part + '='.repeat((4 - part.length % 4) % 4)));
}

function applyToken(token) {
  state.token = token;
  const claims = claimsOf(token);
  state.userId = claims.sub;
  state.role = (claims.groups || ['PLAYER'])[0];

  $('whoami').textContent = `${claims.upn} · ${state.role} · ${state.userId.slice(0, 8)}…`;
  $('whoami').classList.add('on');
  // Calificar exige LEVEL_APPROVE, que solo tienen MODERATOR y ADMIN.
  $('mod-row').hidden = !['MODERATOR', 'ADMIN'].includes(state.role);
}

function clearSession() {
  state.token = state.userId = state.role = null;
  $('whoami').textContent = 'sin sesión';
  $('whoami').classList.remove('on');
  $('mod-row').hidden = true;
}

$('btn-register').onclick = async () => {
  const data = await api('/auth/register', {
    method: 'POST',
    body: {
      username: $('auth-username').value,
      email: $('auth-email').value,
      password: $('auth-password').value,
    },
  });
  applyToken(data.accessToken);
};

$('btn-login').onclick = async () => {
  const data = await api('/auth/login', {
    method: 'POST',
    body: { email: $('auth-email').value, password: $('auth-password').value },
  });
  applyToken(data.accessToken);
};

$('btn-logout').onclick = clearSession;

// --- descubrimiento ----------------------------------------------------------

async function search() {
  const params = new URLSearchParams({ size: '20' });
  const q = $('search-q').value.trim();
  if (q) params.set('q', q);
  // Filtrar por uno mismo es lo unico que permite ver borradores propios en el listado.
  if ($('search-mine').checked && state.userId) params.set('authorId', state.userId);

  const page = await api(`/levels?${params}`, { auth: true });
  const list = $('level-list');
  list.innerHTML = '';

  if (!page.items.length) {
    list.innerHTML = '<div class="empty">Sin resultados.</div>';
    return;
  }
  for (const level of page.items) {
    const item = document.createElement('div');
    item.className = 'item';
    item.innerHTML = `<span>${escapeHtml(level.name)}</span>
      <span class="meta">${level.status} · ♥${level.likes} · ⇩${level.downloads}</span>`;
    item.onclick = () => openLevel(level.id);
    list.append(item);
  }
}

$('btn-search').onclick = search;

// --- creacion ----------------------------------------------------------------

async function loadCatalogs() {
  const songs = await api('/songs?size=100');
  $('new-song').innerHTML = songs.items
    .map((s) => `<option value="${s.id}">${escapeHtml(s.title)} — ${escapeHtml(s.artist)}</option>`)
    .join('');

  const difficulties = await api('/difficulties');
  $('mod-difficulty').innerHTML = difficulties
    .map((d) => `<option value="${d.id}">${escapeHtml(d.name)} (${d.stars}★)</option>`)
    .join('');
}

$('btn-create').onclick = async () => {
  const level = await api('/levels', {
    method: 'POST',
    auth: true,
    body: {
      name: $('new-name').value,
      description: $('new-description').value || null,
      songId: $('new-song').value,
    },
  });
  state.objects.clear();
  renderGrid();
  await openLevel(level.id);
  await search();
};

// --- editor ------------------------------------------------------------------

function buildGrid() {
  const grid = $('grid');
  grid.innerHTML = '';
  for (let y = 0; y < ROWS; y++) {
    for (let x = 0; x < COLS; x++) {
      const cell = document.createElement('div');
      cell.className = 'cell';
      cell.dataset.key = `${x},${y}`;
      grid.append(cell);
    }
  }
  // Pintar arrastrando: mousedown + mouseover mientras el boton siga pulsado.
  grid.onmousedown = (e) => {
    if (e.target.dataset.key) {
      state.painting = true;
      paint(e.target);
    }
  };
  grid.onmouseover = (e) => state.painting && e.target.dataset.key && paint(e.target);
  document.addEventListener('mouseup', () => (state.painting = false));
}

function paint(cell) {
  const key = cell.dataset.key;
  if (state.tool === 'erase') state.objects.delete(key);
  else state.objects.set(key, state.tool);
  renderCell(cell);
  $('editor-count').textContent = `${state.objects.size} objetos`;
}

function renderCell(cell) {
  cell.className = 'cell';
  const type = state.objects.get(cell.dataset.key);
  if (type) cell.classList.add(type);
}

function renderGrid() {
  document.querySelectorAll('.cell').forEach(renderCell);
  $('editor-count').textContent = `${state.objects.size} objetos`;
}

document.querySelectorAll('.tool').forEach((btn) => {
  btn.onclick = () => {
    document.querySelectorAll('.tool').forEach((b) => b.classList.remove('selected'));
    btn.classList.add('selected');
    state.tool = btn.dataset.tool;
  };
});

$('btn-clear').onclick = () => {
  state.objects.clear();
  renderGrid();
};

async function openLevel(levelId) {
  const detail = await api(`/levels/${levelId}`, { auth: true });
  state.level = detail.level;

  $('editor-empty').hidden = true;
  $('editor-body').hidden = false;
  $('editor-name').textContent = detail.level.name;
  $('editor-status').textContent = detail.level.status;
  $('editor-difficulty').hidden = !detail.difficulty;
  if (detail.difficulty) $('editor-difficulty').textContent = `${detail.difficulty.name} ${detail.difficulty.stars}★`;

  const viewer = detail.viewer
    ? ` · rol ${detail.viewer.memberRole ?? '—'}${detail.viewer.liked ? ' · te gusta' : ''}`
    : ' · viewer null (anónimo)';
  $('editor-stats').textContent =
    `♥${detail.stats.likes} ⇩${detail.stats.downloads} ▶${detail.stats.plays}${viewer}`;

  await loadVersions();
  if (detail.level.currentVersionId) await openCurrentVersion();
  else {
    state.objects.clear();
    renderGrid();
  }
}

async function loadVersions() {
  const list = $('version-list');
  list.innerHTML = '';
  try {
    const versions = await api(`/levels/${state.level.id}/versions`, { auth: true });
    if (!versions.length) {
      list.innerHTML = '<div class="empty">Todavía no hay versiones.</div>';
      return;
    }
    for (const v of versions) {
      const item = document.createElement('div');
      item.className = 'item';
      item.innerHTML = `<span>v${v.versionNumber} · ${escapeHtml(v.changelog || 'sin nota')}</span>
        <span class="meta">${v.checksum.slice(0, 12)}…</span>`;
      list.append(item);
    }
  } catch {
    list.innerHTML = '<div class="empty">Sólo los miembros ven el historial.</div>';
  }
}

function toBase64(text) {
  const bytes = new TextEncoder().encode(text);
  let binary = '';
  bytes.forEach((b) => (binary += String.fromCharCode(b)));
  return btoa(binary);
}

$('btn-save').onclick = async () => {
  const objects = [...state.objects].map(([key, type]) => {
    const [x, y] = key.split(',').map(Number);
    return { t: type, x, y };
  });
  // La longitud la calcula el cliente porque el servidor no parsea el formato.
  const length = objects.length ? Math.max(...objects.map((o) => o.x)) + 1 : 1;

  await api(`/levels/${state.level.id}/versions`, {
    method: 'POST',
    auth: true,
    body: {
      levelData: toBase64(JSON.stringify({ format: FORMAT, objects })),
      changelog: $('save-changelog').value || null,
      length,
    },
  });
  await openLevel(state.level.id);
};

async function openCurrentVersion() {
  const buffer = await api(`/levels/${state.level.id}/versions/current`, { auth: true, raw: true });

  // El blob es opaco para el servidor: puede venir de otro editor o de un formato anterior.
  let parsed;
  try {
    parsed = JSON.parse(new TextDecoder().decode(buffer));
  } catch {
    log(false, 'La versión actual no tiene el formato del editor. Se abre un lienzo vacío.');
    state.objects.clear();
    renderGrid();
    return;
  }
  if (parsed.format !== FORMAT) {
    log(false, `Versión guardada con formato ${parsed.format}; este editor entiende el ${FORMAT}.`);
  }

  state.objects.clear();
  for (const o of parsed.objects ?? []) state.objects.set(`${o.x},${o.y}`, o.t);
  renderGrid();
}

$('btn-open').onclick = () => openCurrentVersion();

const action = (id, run) => ($(id).onclick = async () => {
  await run();
  await openLevel(state.level.id);
  await search();
});

action('btn-publish', () => api(`/levels/${state.level.id}/publish`, { method: 'POST', auth: true }));
action('btn-unpublish', () => api(`/levels/${state.level.id}/unpublish`, { method: 'POST', auth: true }));
action('btn-like', () => api(`/levels/${state.level.id}/likes`, { method: 'POST', auth: true }));
action('btn-play', () => api(`/levels/${state.level.id}/plays`, { method: 'POST', auth: true }));
action('btn-rate', () =>
  api(`/levels/${state.level.id}/difficulty`, {
    method: 'PATCH',
    auth: true,
    body: { difficultyId: $('mod-difficulty').value },
  }));

$('btn-delete').onclick = async () => {
  await api(`/levels/${state.level.id}`, { method: 'DELETE', auth: true });
  state.level = null;
  $('editor-body').hidden = true;
  $('editor-empty').hidden = false;
  await search();
};

// --- arranque ----------------------------------------------------------------

function escapeHtml(text) {
  return String(text).replace(/[&<>"']/g, (c) =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
}

buildGrid();
loadCatalogs();
search();
