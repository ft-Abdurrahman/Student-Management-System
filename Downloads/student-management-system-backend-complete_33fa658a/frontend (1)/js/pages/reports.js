
renderLayout('reports','Reports',[{label:'Operations'},{label:'Reports'}]);
const types=[
  ['fa-user-graduate','Student Report','All students with personal, academic, and contact details'],
  ['fa-clipboard-check','Attendance Report','Daily, weekly, monthly attendance by class or student'],
  ['fa-square-poll-vertical','Result Report','Semester results, GPA, CGPA, and transcripts'],
  ['fa-money-bill','Fee Report','Fee collection, pending dues, overdue analysis'],
  ['fa-chalkboard-user','Teacher Report','Workload, classes taken, attendance, leave'],
  ['fa-building-columns','Department Report','Department-wise summary across all metrics'],
];
setPageContent(`
  <div class="stat-grid">
    ${types.map(([ic,t,d])=>`
      <div class="card">
        <div class="flex-between"><div style="width:42px;height:42px;border-radius:10px;background:var(--primary-50);color:var(--primary);display:grid;place-items:center"><i class="fa-solid ${ic}"></i></div>
          <span class="badge info">PDF / XLSX</span></div>
        <h3 style="font-size:15px;margin-top:10px">${t}</h3>
        <p style="font-size:13px;margin-top:4px">${d}</p>
        <div style="display:flex;gap:8px;margin-top:14px">
          <button class="btn"><i class="fa-solid fa-file-pdf"></i> PDF</button>
          <button class="btn"><i class="fa-solid fa-file-excel"></i> Excel</button>
        </div>
      </div>`).join('')}
  </div>

  <div class="card mt-3">
    <div class="card-title"><h3>Recently generated</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Name</th><th>Type</th><th>Format</th><th>Generated</th><th>Size</th><th></th></tr></thead>
      <tbody>${[
        ['Spring-2026 Result Sheet','Result','PDF','2026-06-24 10:12','1.2 MB'],
        ['Fee Defaulters - June','Fee','XLSX','2026-06-22 18:30','244 KB'],
        ['Attendance Summary May','Attendance','PDF','2026-06-01 09:00','680 KB'],
      ].map(r=>`<tr><td><strong>${r[0]}</strong></td><td>${r[1]}</td><td><span class="badge info">${r[2]}</span></td><td>${r[3]}</td><td>${r[4]}</td>
        <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-download"></i></button></td></tr>`).join('')}
      </tbody></table></div>
  </div>`);
