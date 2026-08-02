
renderLayout('courses','Courses',[{label:'Academics'},{label:'Courses'}]);
setPageActions(`<button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Course</button>`);
setPageContent(`
  <div class="stat-grid mb-2">
    ${window.DEMO_COURSES.map(c=>`
      <div class="card">
        <div class="flex-between mb-1"><span class="badge info">${c.code}</span><button class="btn btn-ghost"><i class="fa-solid fa-ellipsis"></i></button></div>
        <h3 style="font-size:16px;margin-bottom:4px">${c.name}</h3>
        <p style="font-size:13px">Department: ${c.department}</p>
        <div class="row mt-2" style="gap:8px">
          <div class="col" style="text-align:center;background:var(--surface-2);border-radius:8px;padding:10px"><div class="text-muted" style="font-size:11px">CREDITS</div><div style="font-weight:600">${c.credits}</div></div>
          <div class="col" style="text-align:center;background:var(--surface-2);border-radius:8px;padding:10px"><div class="text-muted" style="font-size:11px">SEMESTERS</div><div style="font-weight:600">${c.duration}</div></div>
        </div>
      </div>`).join('')}
  </div>

  <div class="card">
    <div class="card-title"><h3>Departments</h3><button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Department</button></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Code</th><th>Department</th><th>Head</th><th>Courses</th><th></th></tr></thead>
      <tbody>
        ${['CSE','ECE','ME','MBA','BCA','MCA','CE','EE'].map((d,i)=>`
        <tr><td><strong>DEP-${d}</strong></td><td>${d} Department</td>
          <td>Dr. ${['Mehta','Khan','Sharma','Iyer','Reddy','Nair','Joshi','Kapoor'][i]}</td>
          <td>${1+Math.floor(Math.random()*4)}</td>
          <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-pen"></i></button></td></tr>`).join('')}
      </tbody></table></div>
  </div>`);
