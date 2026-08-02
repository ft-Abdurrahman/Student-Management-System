
  renderLayout('students', 'Students', [{label:'Academics'},{label:'Students'}]);
  setPageActions(`
    <button class="btn"><i class="fa-solid fa-file-excel"></i> Export Excel</button>
    <button class="btn"><i class="fa-solid fa-file-pdf"></i> Export PDF</button>
    <button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Student</button>
  `);

  setPageContent(`
    <div class="card">
      <div class="toolbar">
        <div class="filters">
          <div class="input-icon"><i class="fa-solid fa-magnifying-glass"></i>
            <input class="input" placeholder="Search by name, roll, email…" id="q"></div>
          <select class="select" id="fDept"><option value="">All departments</option>
            ${['CSE','ECE','ME','MBA','BCA','MCA'].map(d=>`<option>${d}</option>`).join('')}</select>
          <select class="select" id="fSem"><option value="">All semesters</option>
            ${[1,2,3,4,5,6,7,8].map(s=>`<option>${s}</option>`).join('')}</select>
          <select class="select" id="fStatus"><option value="">All status</option>
            <option>ACTIVE</option><option>INACTIVE</option><option>GRADUATED</option></select>
        </div>
        <div class="text-muted" id="count"></div>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead><tr><th>Roll No.</th><th>Name</th><th>Department</th><th>Course</th><th>Sem</th><th>Email</th><th>CGPA</th><th>Status</th><th></th></tr></thead>
          <tbody id="rows"></tbody>
        </table>
      </div>
      <div class="pagination" id="pager"></div>
    </div>
  `);

  let page = 1, size = 10;
  async function refresh() {
    const all = (await SmsApi.students()).content || window.DEMO_STUDENTS;
    const q = document.getElementById('q').value.toLowerCase();
    const dept = document.getElementById('fDept').value;
    const sem  = document.getElementById('fSem').value;
    const st   = document.getElementById('fStatus').value;
    const filtered = all.filter(s =>
      (!q || (s.fullName + s.rollNumber + s.email).toLowerCase().includes(q)) &&
      (!dept || s.department === dept) &&
      (!sem || String(s.semester) === sem) &&
      (!st || s.status === st)
    );
    const { rows, total, pages } = paginate(filtered, page, size);
    document.getElementById('count').textContent = `Showing ${rows.length} of ${total}`;
    document.getElementById('rows').innerHTML = rows.map(s => `
      <tr>
        <td><strong>${s.rollNumber}</strong></td>
        <td><div style="display:flex;gap:10px;align-items:center">
          <div style="width:32px;height:32px;border-radius:50%;background:var(--primary-50);color:var(--primary);display:grid;place-items:center;font-weight:600;font-size:12px">${s.firstName[0]}${s.lastName[0]}</div>
          <div><div>${s.fullName}</div><div class="text-muted" style="font-size:12px">${s.admissionNumber}</div></div></div></td>
        <td><span class="badge info">${s.department}</span></td>
        <td>${s.course}</td>
        <td>${s.semester}</td>
        <td>${s.email}</td>
        <td><strong>${s.cgpa}</strong></td>
        <td><span class="badge ${s.status==='ACTIVE'?'success':s.status==='INACTIVE'?'warning':'info'}">${s.status}</span></td>
        <td class="text-right">
          <button class="btn btn-ghost" title="View"><i class="fa-solid fa-eye"></i></button>
          <button class="btn btn-ghost" title="Edit"><i class="fa-solid fa-pen"></i></button>
        </td>
      </tr>`).join('');
    renderPagination('pager', page, Math.max(1, pages), p => { page = p; refresh(); });
  }
  ['q','fDept','fSem','fStatus'].forEach(id => document.getElementById(id).addEventListener('input', () => { page=1; refresh(); }));
  refresh();
