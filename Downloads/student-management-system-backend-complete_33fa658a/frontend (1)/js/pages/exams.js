
renderLayout('exams','Examinations',[{label:'Operations'},{label:'Exams'}]);
setPageActions(`<button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Schedule Exam</button>`);
setPageContent(`
  <div class="card mb-3">
    <div class="card-title"><h3>Upcoming examinations</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Exam</th><th>Type</th><th>Course</th><th>Semester</th><th>Start</th><th>End</th><th></th></tr></thead>
      <tbody>${window.DEMO_EXAMS.map(e=>`<tr>
        <td><strong>${e.name}</strong></td>
        <td><span class="badge ${e.type==='END'?'danger':e.type==='MID'?'warning':'info'}">${e.type}</span></td>
        <td>${e.course}</td><td>Sem ${e.semester}</td><td>${e.start}</td><td>${e.end}</td>
        <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-eye"></i></button>
          <button class="btn btn-ghost"><i class="fa-solid fa-pen"></i></button></td></tr>`).join('')}
      </tbody></table></div>
  </div>

  <div class="card">
    <div class="card-title"><h3>Exam schedule — Mid-Sem Spring 2026</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Date</th><th>Subject</th><th>Time</th><th>Hall</th><th>Invigilator</th></tr></thead>
      <tbody>${['Data Structures','Algorithms','OS','DBMS','Networks'].map((s,i)=>`
        <tr><td>2026-07-${10+i}</td><td>${s}</td><td>10:00 – 13:00</td><td>Hall ${String.fromCharCode(65+i)}</td><td>Dr. ${['Mehta','Khan','Sharma','Reddy','Nair'][i]}</td></tr>`).join('')}
      </tbody></table></div>
  </div>`);
