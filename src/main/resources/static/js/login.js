document.getElementById("loginForm").addEventListener("submit", async e => {
  e.preventDefault();
  const messageEl = document.getElementById("message");

  const data = new URLSearchParams({
    username: e.target.username.value,
    password: e.target.password.value
  });

  const res = await fetch("/login", { method: "POST", credentials:"include", body: data });
  const text = await res.text();
  messageEl.textContent = text;
  messageEl.style.display = "block";
  messageEl.className = `message ${res.ok ? 'success' : 'error'}`;
  if (res.ok) setTimeout(() => location.href = "index.html", 800);
});
