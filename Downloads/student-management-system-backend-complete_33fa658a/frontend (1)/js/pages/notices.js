
renderLayout('notices','Notice Board',[{label:'Operations'},{label:'Notices'}]);
setPageActions(`<button class="btn btn-primary"><i class="fa-solid fa-bullhorn"></i> Publish Notice</button>`);
setPageContent(`
  <div class="row">
    <div class="col" style="flex:2">
      ${window.DEMO_NOTICES.map(n=>`
        <div class="card mb-2">
          <div class="flex-between">
            <div style="display:flex;gap:10px;align-items:center">
              <div style="width:42px;height:42px;border-radius:10px;background:var(--primary-50);color:var(--primary);display:grid;place-items:center"><i class="fa-solid fa-bullhorn"></i></div>
              <div>
                <h3 style="font-size:15px">${n.title}${n.pinned?' <i class="fa-solid fa-thumbtack" style="color:var(--warning);margin-left:6px"></i>':''}</h3>
                <p style="font-size:12px">${n.author} · ${n.publishAt} · <span class="badge info">${n.audience}</span></p>
              </div>
            </div>
            <button class="btn btn-ghost"><i class="fa-solid fa-ellipsis"></i></button>
          </div>
        </div>`).join('')}
    </div>
    <div class="col card">
      <div class="card-title"><h3>Quick publish</h3></div>
      <div class="field"><label>Title</label><input class="input" placeholder="Notice title"></div>
      <div class="field"><label>Audience</label>
        <select class="select"><option>All</option><option>Students only</option><option>Teachers only</option><option>Department-specific</option></select></div>
      <div class="field"><label>Body</label><textarea class="textarea" rows="5" placeholder="Write a notice…"></textarea></div>
      <label style="display:flex;gap:8px;font-size:13px;color:var(--text-muted);align-items:center;margin-bottom:12px"><input type="checkbox"> Pin this notice</label>
      <button class="btn btn-primary btn-block">Publish</button>
    </div>
  </div>`);
