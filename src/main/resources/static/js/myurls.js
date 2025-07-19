(async () => {
  const res = await fetch("/myurls", { credentials: "include" });
  if (!res.ok) {
    document.body.insertAdjacentHTML("beforeend", `<p class="message error">Please login to view URLs.</p>`);
    return;
  }

  const rows = await res.json();
  const body = document.getElementById("tableBody");

  rows.forEach(r => {
    const linkHTML = `<a class="short-link" href="/s/${r.slug}" target="_blank">/${r.slug}</a>`;
    body.insertAdjacentHTML("beforeend", `<tr><td>${linkHTML}</td><td><a href="${r.longUrl}" target="_blank">${r.longUrl}</a></td></tr>`);
  });

  document.getElementById("logoutBtn").addEventListener("click", ()=> {
    document.cookie = "user=; Max-Age=0; path=/";
    window.location.href = "index.html";
  });
})();
