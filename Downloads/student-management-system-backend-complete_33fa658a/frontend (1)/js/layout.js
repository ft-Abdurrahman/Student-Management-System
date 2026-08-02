/* layout.js — injects sidebar/header into every dashboard page and provides theme + auth helpers */

const SMS_NAV = [
  { label: 'OVERVIEW', section: true },
  { id: 'dashboard',   icon: 'fa-gauge-high',        label: 'Dashboard',          href: 'dashboard.html' },
  { label: 'ACADEMICS', section: true },
  { id: 'students',    icon: 'fa-user-graduate',     label: 'Students',           href: 'students.html' },
  { id: 'teachers',    icon: 'fa-chalkboard-user',   label: 'Teachers',           href: 'teachers.html' },
  { id: 'courses',     icon: 'fa-book-open',         label: 'Courses',            href: 'courses.html' },
  { id: 'subjects',    icon: 'fa-book',              label: 'Subjects',           href: 'subjects.html' },
  { id: 'timetable',   icon: 'fa-calendar-days',     label: 'Timetable',          href: 'timetable.html' },
  { label: 'OPERATIONS', section: true },
  { id: 'attendance',  icon: 'fa-clipboard-check',   label: 'Attendance',         href: 'attendance.html' },
  { id: 'exams',       icon: 'fa-file-pen',          label: 'Exams',              href: 'exams.html' },
  { id: 'results',     icon: 'fa-square-poll-vertical', label: 'Results',         href: 'results.html' },
  { id: 'fees',        icon: 'fa-money-bill',        label: 'Fees',               href: 'fees.html' },
  { id: 'notices',     icon: 'fa-bullhorn',          label: 'Notices',            href: 'notices.html' },
  { id: 'reports',     icon: 'fa-chart-line',        label: 'Reports',            href: 'reports.html' },
  { label: 'ACCOUNT', section: true },
  { id: 'profile',     icon: 'fa-user',              label: 'Profile',            href: 'profile.html' },
  { id: 'settings',    icon: 'fa-gear',              label: 'Settings',           href: 'settings.html' },
];

function getUser() {
  try { return JSON.parse(localStorage.getItem('sms.user')) || demoUser(); }
  catch { return demoUser(); }
}
function demoUser() {
  return { name: 'Ananya Sharma', email: 'admin@sms.edu', role: 'ADMIN', initials: 'AS' };
}
function initials(name) {
  return (name || '').split(' ').map(s => s[0]).slice(0,2).join('').toUpperCase() || 'U';
}

function renderLayout(pageId, pageTitle, crumbs = []) {
  const user = getUser();
  const navHtml = SMS_NAV.map(n => {
    if (n.section) return `<div class="section-label">${n.label}</div>`;
    return `<a href="${n.href}" data-nav="${n.id}" class="${n.id === pageId ? 'active' : ''}">
              <i class="fa-solid ${n.icon}"></i><span>${n.label}</span>
            </a>`;
  }).join('');

  const crumbHtml = crumbs.length
    ? `<div class="crumbs">${crumbs.map((c,i)=> i===crumbs.length-1
        ? `<span>${c.label}</span>`
        : `<a href="${c.href || '#'}">${c.label}</a> <i class="fa-solid fa-angle-right" style="font-size:10px"></i>`).join(' ')}</div>`
    : '';

  const shell = `
    <div class="app">
      <aside class="sidebar" id="sidebar">
        <div class="brand">
          <div class="logo">S</div>
          <div>
            <div class="name">SMS University</div>
            <div class="sub">Student Mgmt System</div>
          </div>
        </div>
        <nav class="nav">${navHtml}</nav>
        <div class="user-mini">
          <div class="avatar">${initials(user.name)}</div>
          <div>
            <div class="name">${user.name}</div>
            <div class="role">${user.role}</div>
          </div>
        </div>
      </aside>
      <header class="header">
        <div style="display:flex;align-items:center;gap:10px;flex:1">
          <button class="icon-btn sidebar-toggle" id="sbToggle"><i class="fa-solid fa-bars"></i></button>
          <div class="search">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input placeholder="Search students, teachers, courses…">
          </div>
        </div>
        <div class="actions">
          <button class="icon-btn" id="themeBtn" title="Toggle theme"><i class="fa-solid fa-moon"></i></button>
          <button class="icon-btn" title="Notifications" style="position:relative">
            <i class="fa-regular fa-bell"></i><span class="dot"></span>
          </button>
          <div class="user">
            <div class="avatar">${initials(user.name)}</div>
            <div><div class="name">${user.name}</div></div>
            <button class="icon-btn" id="logoutBtn" title="Logout" style="border:none"><i class="fa-solid fa-arrow-right-from-bracket"></i></button>
          </div>
        </div>
      </header>
      <main class="main">
        <div class="page-head">
          <div>
            <h1>${pageTitle}</h1>
            ${crumbHtml}
          </div>
          <div id="pageActions"></div>
        </div>
        <div id="pageContent"></div>
      </main>
    </div>
    <div class="backdrop" id="backdrop"></div>
    <div id="toast-stack"></div>
  `;
  document.body.innerHTML = shell + document.body.innerHTML;

  // mobile sidebar
  const sb = document.getElementById('sidebar');
  const bd = document.getElementById('backdrop');
  document.getElementById('sbToggle').onclick = () => { sb.classList.toggle('open'); bd.classList.toggle('show'); };
  bd.onclick = () => { sb.classList.remove('open'); bd.classList.remove('show'); };

  // theme
  applyTheme(localStorage.getItem('sms.theme') || 'light');
  document.getElementById('themeBtn').onclick = () => {
    const next = (localStorage.getItem('sms.theme') || 'light') === 'light' ? 'dark' : 'light';
    localStorage.setItem('sms.theme', next); applyTheme(next);
  };

  // logout
  document.getElementById('logoutBtn').onclick = () => {
    localStorage.removeItem('sms.access');
    localStorage.removeItem('sms.refresh');
    localStorage.removeItem('sms.user');
    location.href = 'login.html';
  };
}

function applyTheme(mode) {
  document.documentElement.setAttribute('data-theme', mode);
  const btn = document.querySelector('#themeBtn i');
  if (btn) btn.className = mode === 'dark' ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
}

function setPageActions(html) { document.getElementById('pageActions').innerHTML = html; }
function setPageContent(html) { document.getElementById('pageContent').innerHTML = html; }

/* ---- Toast ---- */
function toast(msg, type = 'info') {
  const stack = document.getElementById('toast-stack') || (() => {
    const s = document.createElement('div'); s.id = 'toast-stack'; document.body.appendChild(s); return s;
  })();
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  const icons = { success:'fa-circle-check', danger:'fa-circle-xmark', warning:'fa-triangle-exclamation', info:'fa-circle-info' };
  el.innerHTML = `<i class="fa-solid ${icons[type]}" style="color:var(--${type==='info'?'primary':type})"></i><div class="msg">${msg}</div>`;
  stack.appendChild(el);
  setTimeout(() => { el.style.opacity = '0'; el.style.transform = 'translateX(20px)'; setTimeout(()=>el.remove(), 250); }, 3500);
}

/* ---- Pagination helper ---- */
function paginate(items, page, size) {
  const start = (page - 1) * size;
  return { rows: items.slice(start, start + size), total: items.length, pages: Math.ceil(items.length / size) };
}
function renderPagination(elId, page, pages, onChange) {
  const el = document.getElementById(elId); if (!el) return;
  let html = `<button ${page===1?'disabled':''} data-p="${page-1}"><i class="fa-solid fa-chevron-left"></i></button>`;
  for (let i = 1; i <= pages; i++) html += `<button class="${i===page?'active':''}" data-p="${i}">${i}</button>`;
  html += `<button ${page===pages?'disabled':''} data-p="${page+1}"><i class="fa-solid fa-chevron-right"></i></button>`;
  el.innerHTML = html;
  el.querySelectorAll('button[data-p]').forEach(b => b.onclick = () => onChange(Number(b.dataset.p)));
}
