
  // tab switching
  let channel = 'EMAIL';
  document.querySelectorAll('#channelTabs button').forEach(b => {
    b.onclick = () => {
      document.querySelectorAll('#channelTabs button').forEach(x => x.classList.remove('active'));
      b.classList.add('active');
      channel = b.dataset.channel;
      document.getElementById('emailField').classList.toggle('hidden', channel !== 'EMAIL');
      document.getElementById('phoneField').classList.toggle('hidden', channel !== 'SMS');
    };
  });

  document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    const destination = channel === 'EMAIL' ? f.email.value.trim() : f.phone.value.trim();
    if (!destination) return toast('Please enter your ' + (channel === 'EMAIL' ? 'email' : 'phone'), 'warning');

    const btn = document.getElementById('sendBtn');
    btn.disabled = true; btn.innerHTML = '<div class="spinner sm" style="border-color:rgba(255,255,255,.5);border-top-color:#fff"></div> Sending...';
    try {
      const res = await api('/auth/otp/request', { method: 'POST', auth: false, body: { destination, channel } });
      sessionStorage.setItem('sms.otp.id', res.otpId);
      sessionStorage.setItem('sms.otp.destination', destination);
      sessionStorage.setItem('sms.otp.channel', channel);
      sessionStorage.setItem('sms.otp.expiresAt', String(Date.now() + (res.expiresIn || 300) * 1000));
      location.href = 'otp.html';
    } catch (err) {
      // demo fallback if backend not running
      if (err.status === 0) {
        sessionStorage.setItem('sms.otp.id', 'demo-' + Date.now());
        sessionStorage.setItem('sms.otp.destination', destination);
        sessionStorage.setItem('sms.otp.channel', channel);
        sessionStorage.setItem('sms.otp.expiresAt', String(Date.now() + 300000));
        sessionStorage.setItem('sms.otp.demo', '123456');
        toast('Backend offline — using demo OTP 123456', 'info');
        setTimeout(() => location.href = 'otp.html', 800);
        return;
      }
      toast(err.message || 'Could not send OTP', 'danger');
      btn.disabled = false; btn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Send OTP';
    }
  });
