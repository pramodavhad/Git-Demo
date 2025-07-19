document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("shortenForm");
  const message = document.getElementById("message");
  const customSlugContainer = document.getElementById("customSlugContainer");

  // Check login from cookie
  const user = getLoggedInUser();
  if (user) {
    customSlugContainer.style.display = "block";
    fetchUserUrls(user); // Phase 6
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const url = document.getElementById("url").value.trim();
    const customSlug = document.getElementById("customSlug").value.trim();

    const formData = new URLSearchParams();
    formData.append("url", url);
    if (customSlug) formData.append("customSlug", customSlug);

    try {
      const res = await fetch("http://localhost:8080/shorten", {
        method: "POST",
        body: formData,
        credentials: "include"
      });
      const text = await res.text();
      message.innerText = text;
      message.style.color = res.ok ? "green" : "red";
    } catch (err) {
      message.innerText = "Error shortening URL.";
      message.style.color = "red";
    }
  });
});

function getLoggedInUser() {
  const cookie = document.cookie.split(";").find(c => c.trim().startsWith("user="));
  return cookie ? cookie.split("=")[1] : null;
}

function logout() {
  document.cookie = "user=; Max-Age=0; path=/";
  location.reload();
}

async function fetchUserUrls(user) {
  const tableBody = document.querySelector("#userUrlsTable tbody");
  const container = document.getElementById("userUrlsContainer");

  try {
    const res = await fetch("http://localhost:8080/myurls", {
      method: "GET",
      credentials: "include"
    });
    if (!res.ok) throw new Error("Failed to fetch URLs");

    const urls = await res.json();
    tableBody.innerHTML = "";

    urls.forEach(row => {
      const tr = document.createElement("tr");
      const shortLink = `http://localhost:8080/s/${row.slug}`;
      tr.innerHTML = `
        <td><a href="${shortLink}" target="_blank">${row.slug}</a></td>
        <td><a href="${row.longUrl}" target="_blank">${row.longUrl}</a></td>
      `;
      tableBody.appendChild(tr);
    });

    container.style.display = "block";
  } catch (err) {
    console.error(err);
  }
}
