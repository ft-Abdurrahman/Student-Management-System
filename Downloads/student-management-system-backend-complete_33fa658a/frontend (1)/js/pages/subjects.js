
renderLayout('subjects','Subjects',[{label:'Academics'},{label:'Subjects'}]);
setPageActions(`<button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Subject</button>`);
setPageContent(`
  <div class="card">
    <div class="toolbar">
      <div class="filters">
        <div class="input-icon"><i class="fa-solid fa-magnifying-glass"></i><input class="input" placeholder="Search…" id="q"></div>
        <select class="select" id="sem"><option value="">All semesters</option>${[1,2,3,4,5,6,7,8].map(s=>`<option>${s}</option>`).join('')}</select>
      </div>
    </div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Code</th><th>Subject</th><th>Credits</th><th>Semester</th><th>Course</th><th>Assigned Teacher</th><th></th></tr></thead>
      <tbody id="rows"></tbody></table></div>
  </div>`);
function refresh(){
  const q=document.getElementById('q').value.toLowerCase();
  const sem=document.getElementById('sem').value;
  const f=window.DEMO_SUBJECTS.filter(s=>(!q||(s.name+s.code).toLowerCase().includes(q))&&(!sem||String(s.semester)===sem));
  document.getElementById('rows').innerHTML=f.map(s=>`<tr>
    <td><strong>${s.code}</strong></td><td>${s.name}</td><td>${s.credits}</td><td>${s.semester}</td>
    <td>${s.course}</td><td>${s.teacher}</td>
    <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-user-plus"></i></button>
      <button class="btn btn-ghost"><i class="fa-solid fa-pen"></i></button></td></tr>`).join('');
}
['q','sem'].forEach(id=>document.getElementById(id).addEventListener('input',refresh));
refresh();
