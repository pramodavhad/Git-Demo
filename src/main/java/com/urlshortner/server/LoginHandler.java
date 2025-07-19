package com.urlshortner.server;

import com.sun.net.httpserver.*;
import com.urlshortner.service.AuthService;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            logger.warn("Received non-POST request on /login from {}", exchange.getRemoteAddress());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null) {
                logger.warn("Empty request body on /login from {}", exchange.getRemoteAddress());
                sendResponse(exchange, 400, "Invalid request");
                return;
            }

            Map<String, String> formData = parseFormData(line);
            String username = formData.get("username");
            String password = formData.get("password");

            if (username == null || password == null) {
                logger.warn("Missing username or password in login request from {}", exchange.getRemoteAddress());
                sendResponse(exchange, 400, "Missing credentials");
                return;
            }

            logger.info("Login attempt for user '{}'", username);

            boolean authenticated = authService.login(username, password);
            if (authenticated) {
                logger.info("Login successful for user '{}'", username);
                exchange.getResponseHeaders().add("Set-Cookie", "user=" + username + "; Path=/");
                sendResponse(exchange, 200, "Login successful");
            } else {
                logger.warn("Login failed for user '{}'", username);
                sendResponse(exchange, 401, "Invalid credentials");
            }

        } catch (Exception e) {
            logger.error("Error handling login request", e);
            sendResponse(exchange, 500, "Internal server error");
        }
    }

    private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        for (String pair : formData.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
