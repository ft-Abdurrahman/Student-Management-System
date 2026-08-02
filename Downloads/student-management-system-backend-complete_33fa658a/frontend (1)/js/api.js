/* api.js — thin wrapper around fetch that adds the JWT and the response envelope */

const API_BASE = (window.SMS_API_BASE || 'http://localhost:8080/api/v1');

async function api(path, { method = 'GET', body, auth = true, headers = {} } = {}) {
  const opts = { method, headers: { 'Content-Type': 'application/json', ...headers } };
  if (auth) {
    const tok = localStorage.getItem('sms.access');
    if (tok) opts.headers['Authorization'] = `Bearer ${tok}`;
  }
  if (body !== undefined) opts.body = JSON.stringify(body);

  let res;
  try { res = await fetch(API_BASE + path, opts); }
  catch (e) { throw { status: 0, message: 'Network error — is the gateway running?' }; }

  let json = null;
  try { json = await res.json(); } catch {}

  if (res.status === 401 && auth) {
    // try refresh once
    if (!path.startsWith('/auth/refresh') && await tryRefresh()) {
      return api(path, { method, body, auth, headers });
    }
    localStorage.removeItem('sms.access');
    if (!location.pathname.endsWith('login.html')) location.href = 'login.html';
    return;
  }

  if (!res.ok) {
    const msg = (json && (json.message || (json.errors && json.errors[0]?.message))) || `Request failed (${res.status})`;
    throw { status: res.status, message: msg, payload: json };
  }
  return (json && 'data' in json) ? json.data : json;
}

async function tryRefresh() {
  const rt = localStorage.getItem('sms.refresh');
  if (!rt) return false;
  try {
    const res = await fetch(API_BASE + '/auth/refresh', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken: rt })
    });
    if (!res.ok) return false;
    const json = await res.json();
    const data = json.data || json;
    localStorage.setItem('sms.access', data.accessToken);
    if (data.refreshToken) localStorage.setItem('sms.refresh', data.refreshToken);
    return true;
  } catch { return false; }
}

/* Convenience domain wrappers — fall back to demo data if backend is offline */
const SmsApi = {
  async students(query = {}) {
    try { return await api('/students?' + new URLSearchParams(query)); }
    catch { return { content: window.DEMO_STUDENTS || [], totalElements: (window.DEMO_STUDENTS||[]).length }; }
  },
  async teachers() {
    try { return await api('/teachers'); }
    catch { return { content: window.DEMO_TEACHERS || [] }; }
  },
  async dashboardSummary() {
    try { return await api('/dashboard/summary'); }
    catch { return window.DEMO_DASHBOARD; }
  }
};
