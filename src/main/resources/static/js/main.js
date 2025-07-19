document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("shortenForm");
  const messageEl = document.getElementById("message");
  const customInput = document.getElementById("customSlug");
  const myurlsLink = document.getElementById("myurlsLink");
  const logoutBtn = document.getElementById("logoutBtn");

  const user = document.cookie.split("; ").find(c=>c.startsWith("user="))?.split("=")[1];
  if (user) {
    customInput.style.display = "block";
    myurlsLink.style.display = "inline";
    logoutBtn.style.display = "inline";
  }

  logoutBtn.addEventListener("click", ()=> {
    document.cookie = "user=; Max-Age=0; path=/";
    location.reload();
  });

  form.addEventListener("submit", async e => {
    e.preventDefault();
    messageEl.style.display = "none";

    const data = new URLSearchParams({ url: form.url.value });
    if (customInput.value) data.append("customSlug", customInput.value);

    const res = await fetch("/shorten", { method:"POST", credentials:"include", body: data });
    const text = await res.text();
    messageEl.textContent = text;
    messageEl.style.display = "block";
    messageEl.className = `message ${res.ok ? 'success' : 'error'}`;
  });
});
