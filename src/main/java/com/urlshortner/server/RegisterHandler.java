package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortner.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RegisterHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String formLine = reader.readLine();
            if (formLine == null || !formLine.contains("username=") || !formLine.contains("password=")) {
                logger.warn("Malformed registration form submission");
                sendResponse(exchange, 400, "Bad Request");
                return;
            }

            String[] formData = formLine.split("&");
            String username = URLDecoder.decode(formData[0].split("=")[1], StandardCharsets.UTF_8);
            String password = URLDecoder.decode(formData[1].split("=")[1], StandardCharsets.UTF_8);

            logger.info("Registration attempt for user: {}", username);
            boolean success = authService.register(username, password);

            if (success) {
                logger.info("User registered successfully: {}", username);
                sendResponse(exchange, 200, "Registration successful");
            } else {
                logger.warn("Registration failed - username already exists: {}", username);
                sendResponse(exchange, 200, "Username already exists");
            }

        } catch (Exception e) {
            logger.error("Exception during registration", e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
