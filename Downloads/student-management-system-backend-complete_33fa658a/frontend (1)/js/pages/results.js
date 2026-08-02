
renderLayout('results','Results & Grades',[{label:'Operations'},{label:'Results'}]);
setPageActions(`<button class="btn"><i class="fa-solid fa-upload"></i> Bulk Marks</button>
  <button class="btn btn-primary"><i class="fa-solid fa-file-pdf"></i> Publish Result</button>`);
setPageContent(`
  <div class="stat-grid mb-2">
    <div class="stat-card success"><div class="icon"><i class="fa-solid fa-trophy"></i></div><div><div class="label">Pass %</div><div class="value">91.2%</div></div></div>
    <div class="stat-card"><div class="icon"><i class="fa-solid fa-square-poll-vertical"></i></div><div><div class="label">Average CGPA</div><div class="value">7.84</div></div></div>
    <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-medal"></i></div><div><div class="label">Distinctions</div><div class="value">142</div></div></div>
    <div class="stat-card danger"><div class="icon"><i class="fa-solid fa-circle-exclamation"></i></div><div><div class="label">Backlogs</div><div class="value">28</div></div></div>
  </div>
  <div class="card">
    <div class="card-title"><h3>Semester Results</h3>
      <select class="select" style="width:auto"><option>Spring 2026 - Sem 4</option><option>Fall 2025 - Sem 3</option></select></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Roll No.</th><th>Student</th><th>Sem</th><th>SGPA</th><th>CGPA</th><th>Status</th><th></th></tr></thead>
      <tbody>${window.DEMO_RESULTS.map(r=>`<tr>
        <td><strong>${r.rollNumber}</strong></td><td>${r.name}</td><td>${r.semester}</td>
        <td><strong>${r.sgpa}</strong></td><td>${r.cgpa}</td>
        <td><span class="badge ${r.status==='PASS'?'success':'danger'}">${r.status}</span></td>
        <td class="text-right"><button class="btn btn-ghost" title="Transcript"><i class="fa-solid fa-file-pdf"></i></button></td></tr>`).join('')}
      </tbody></table></div>
  </div>`);
