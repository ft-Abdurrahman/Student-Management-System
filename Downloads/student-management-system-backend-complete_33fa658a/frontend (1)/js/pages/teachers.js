
renderLayout('teachers', 'Teachers', [{label:'Academics'},{label:'Teachers'}]);
setPageActions(`<button class="btn"><i class="fa-solid fa-file-excel"></i> Export</button>
  <button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Teacher</button>`);
setPageContent(`
  <div class="card">
    <div class="toolbar">
      <div class="filters">
        <div class="input-icon"><i class="fa-solid fa-magnifying-glass"></i><input class="input" id="q" placeholder="Search…"></div>
        <select class="select" id="dept"><option value="">All departments</option>${['CSE','ECE','ME','MBA','BCA','MCA'].map(d=>`<option>${d}</option>`).join('')}</select>
      </div>
    </div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Code</th><th>Name</th><th>Department</th><th>Qualification</th><th>Experience</th><th>Salary</th><th>Status</th><th></th></tr></thead>
      <tbody id="rows"></tbody></table></div>
    <div class="pagination" id="pager"></div>
  </div>`);
let page=1,size=10;
function refresh(){
  const all=window.DEMO_TEACHERS, q=q1.value.toLowerCase(), d=dept.value;
  const f=all.filter(t=>(!q||(t.fullName+t.employeeCode+t.email).toLowerCase().includes(q))&&(!d||t.department===d));
  const p=paginate(f,page,size);
  rows.innerHTML=p.rows.map(t=>`<tr>
    <td><strong>${t.employeeCode}</strong></td>
    <td><div style="display:flex;gap:10px;align-items:center"><div style="width:32px;height:32px;border-radius:50%;background:#ccfbf1;color:var(--accent);display:grid;place-items:center;font-weight:600;font-size:12px">${t.fullName.split(' ').map(x=>x[0]).join('')}</div>
    <div><div>${t.fullName}</div><div class="text-muted" style="font-size:12px">${t.email}</div></div></div></td>
    <td><span class="badge info">${t.department}</span></td>
    <td>${t.qualification}</td><td>${t.experience} yrs</td>
    <td>₹${t.salary.toLocaleString('en-IN')}</td>
    <td><span class="badge success">${t.status}</span></td>
    <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-pen"></i></button></td></tr>`).join('');
  renderPagination('pager',page,Math.max(1,p.pages),x=>{page=x;refresh();});
}
const q1=document.getElementById('q');const dept=document.getElementById('dept');
[q1,dept].forEach(e=>e.addEventListener('input',()=>{page=1;refresh();}));
refresh();
