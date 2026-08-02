
  const otpId   = sessionStorage.getItem('sms.otp.id');
  const dest    = sessionStorage.getItem('sms.otp.destination');
  const expires = Number(sessionStorage.getItem('sms.otp.expiresAt') || 0);
  if (!otpId) location.href = 'login.html';
  document.getElementById('dest').textContent = dest || 'your account';

  // OTP boxes auto-advance and paste handling
  const boxes = [...document.querySelectorAll('#otpInputs input')];
  boxes.forEach((b, i) => {
    b.addEventListener('input', e => {
      e.target.value = e.target.value.replace(/\D/g,'').slice(0,1);
      if (e.target.value && i < boxes.length-1) boxes[i+1].focus();
    });
    b.addEventListener('keydown', e => {
      if (e.key === 'Backspace' && !b.value && i > 0) boxes[i-1].focus();
    });
    b.addEventListener('paste', e => {
      const data = (e.clipboardData.getData('text') || '').replace(/\D/g,'').slice(0, 6);
      if (!data) return; e.preventDefault();
      data.split('').forEach((c, idx) => boxes[idx] && (boxes[idx].value = c));
      boxes[Math.min(data.length, boxes.length-1)].focus();
    });
  });

  // Timer
  const tEl = document.getElementById('timer');
  const tick = () => {
    const ms = Math.max(0, expires - Date.now());
    const m = Math.floor(ms/60000), s = Math.floor((ms%60000)/1000);
    tEl.textContent = `${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`;
    if (ms === 0) tEl.style.color = 'var(--danger)';
  };
  tick(); setInterval(tick, 1000);

  // Resend (with 30s cooldown)
  let cooldownUntil = Date.now() + 30000;
  const resend = document.getElementById('resendLink');
  const updateResend = () => {
    const left = Math.max(0, cooldownUntil - Date.now());
    if (left > 0) { resend.textContent = `Resend in ${Math.ceil(left/1000)}s`; resend.style.pointerEvents='none'; resend.style.opacity='.5'; }
    else { resend.textContent = 'Resend code'; resend.style.pointerEvents='all'; resend.style.opacity='1'; }
  };
  updateResend(); setInterval(updateResend, 1000);
  resend.onclick = async (e) => {
    e.preventDefault(); if (Date.now() < cooldownUntil) return;
    try { await api('/auth/otp/resend', { method:'POST', auth:false, body:{ otpId } }); }
    catch {}
    cooldownUntil = Date.now() + 30000;
    sessionStorage.setItem('sms.otp.expiresAt', String(Date.now() + 300000));
    location.reload();
  };

  document.getElementById('otpForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const code = boxes.map(b => b.value).join('');
    if (code.length !== 6) return toast('Please enter all 6 digits', 'warning');
    const remember = document.getElementById('remember').checked;
    const deviceId = localStorage.getItem('sms.deviceId') || (crypto.randomUUID && crypto.randomUUID()) || ('dev-' + Date.now());
    localStorage.setItem('sms.deviceId', deviceId);

    const btn = document.getElementById('verifyBtn');
    btn.disabled = true; btn.innerHTML = '<div class="spinner sm" style="border-color:rgba(255,255,255,.5);border-top-color:#fff"></div> Verifying...';
    try {
      const res = await api('/auth/otp/verify', { method:'POST', auth:false,
        body: { otpId, code, deviceId, deviceName: navigator.userAgent.slice(0,80), remember } });
      localStorage.setItem('sms.access', res.accessToken);
      localStorage.setItem('sms.refresh', res.refreshToken);
      localStorage.setItem('sms.user', JSON.stringify(res.user));
      location.href = 'dashboard.html';
    } catch (err) {
      // demo fallback
      const demoCode = sessionStorage.getItem('sms.otp.demo');
      if (err.status === 0 && demoCode && code === demoCode) {
        localStorage.setItem('sms.access', 'demo-access');
        localStorage.setItem('sms.refresh', 'demo-refresh');
        localStorage.setItem('sms.user', JSON.stringify({ name: 'Ananya Sharma', email: dest, role: 'ADMIN' }));
        location.href = 'dashboard.html'; return;
      }
      toast(err.message || 'Verification failed', 'danger');
      btn.disabled = false; btn.innerHTML = '<i class="fa-solid fa-arrow-right-to-bracket"></i> Verify &amp; Sign in';
    }
  });
