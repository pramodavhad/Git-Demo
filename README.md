# 🔗 URL Shortener (No Frameworks)

A lightweight URL shortening service inspired by TinyURL. Built using **Java (no frameworks)** with a clean **HTML/CSS/JS UI**, and backed by **H2 DB**, **SLF4J**, **JUnit 5**, and **GitHub Actions CI**.

---

## ✨ Features

- 🔐 **User Registration & Login**
- 🔗 **Anonymous URL Shortening**
- ✍️ **Custom Short URLs (for logged-in users)**
- 📃 **URL History (user-specific listing)**
- 🛠️ **Plain HTML, CSS, and JS (no frameworks)**
- ⚙️ **SLF4J Logging**
- ✅ **JUnit 5 & Mockito Testing**
- 🚀 **CI/CD with GitHub Actions**

---

## 🏗️ Tech Stack

| Layer        | Tech Used                               |
|--------------|------------------------------------------|
| Backend      | Java + `com.sun.net.httpserver.HttpServer` |
| Database     | H2 (in-memory) with JDBC                 |
| UI           | HTML, CSS, Vanilla JavaScript            |
| Logging      | SLF4J                                    |
| Testing      | JUnit 5, Mockito                         |
| CI/CD        | GitHub Actions                           |

---

## 🚀 How to Run

```bash
# Compile and Package
mvn clean install

# Run Server
java -jar target/url-shortener.jar

# Then visit
http://localhost:8080
```

---

## 📁 Project Structure

```bash
src/
├── main/
│   ├── java/
│   │   ├── com.urlshortener.handlers/
│   │   ├── com.urlshortener.auth/
│   │   ├── com.urlshortener.db/
│   │   ├── com.urlshortener.util/
│   │   └── Main.java
│   └── resources/
│       └── static/ (HTML/CSS/JS)
├── test/
│   └── com.urlshortener.test/
.github/
└── workflows/
    └── ci.yml
```

---

## 🧪 Testing

Run tests with:

```bash
mvn test
```

- Mocked DB interactions with Mockito
- Unit tests for handlers and logic

---

## 🔄 CI/CD via GitHub Actions

- ✅ Runs on push & PR
- 🧪 Validates build and tests using Maven
- 📍 See `.github/workflows/ci.yml`

---

## 👨‍💻 Git Workflow

- Feature isolation via branches (e.g., `feature/login`, `feature/history`)
- Self-review via Pull Requests
- Tasks tracked using GitHub Issues
- CI validation before merge to `main`

---

## 📜 License

This is a personal practice project created for learning purposes.
Feel free to explore and learn from the code.
No official license is associated with this repository.

---

## 🙋‍♂️ Author

**Pramod Avhad**

- GitHub: [@pramodavhad](https://github.com/pramodavhad)
- LinkedIn: [Pramod Avhad](https://linkedin.com/in/pramod-avhad-bab8b6291)


---
