document.getElementById("registerForm").addEventListener("submit", async e => {
  e.preventDefault();
  const messageEl = document.getElementById("message");

  const data = new URLSearchParams({
    username: e.target.username.value,
    password: e.target.password.value
  });

  const res = await fetch("/register", { method: "POST", body: data });
  const text = await res.text();
  messageEl.textContent = text;
  messageEl.style.display = "block";
  messageEl.className = `message ${res.ok ? 'success' : 'error'}`;
  if (res.ok) setTimeout(() => location.href = "login.html", 800);
});
