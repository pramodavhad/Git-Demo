package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortner.service.UrlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UrlHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(UrlHandler.class);
    private final UrlService urlService = new UrlService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("Received URL shortening request from {}", exchange.getRemoteAddress());

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            logger.warn("Invalid request method: {}", exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null || !line.contains("url=")) {
                logger.warn("Missing required form data");
                sendResponse(exchange, 400, "Bad Request: Missing URL field");
                return;
            }

            String longUrl = null;
            String customSlug = null;

            for (String pair : line.split("&")) {
                String[] keyVal = pair.split("=");
                if (keyVal.length < 2) continue;

                String key = keyVal[0];
                String value = URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8);

                if ("url".equals(key)) {
                    longUrl = value;
                } else if ("customSlug".equals(key)) {
                    customSlug = value;
                }
            }

            if (longUrl == null || longUrl.isBlank()) {
                logger.warn("Empty long URL provided");
                sendResponse(exchange, 400, "URL cannot be empty");
                return;
            }

            String user = getLoggedInUser(exchange);
            String response;

            if (customSlug != null && !customSlug.isBlank()) {
                if (user == null) {
                    logger.warn("Unauthorized attempt to use custom slug");
                    sendResponse(exchange, 401, "You must be logged in to use a custom slug.");
                    return;
                }

                boolean saved = urlService.shortenUrlWithCustomSlug(longUrl, customSlug, user);
                if (saved) {
                    response = "Custom Short URL: http://localhost:8080/s/" + customSlug;
                    logger.info("Custom URL created by {}: {}", user, response);
                    sendResponse(exchange, 200, response);
                } else {
                    logger.warn("Slug already exists: {}", customSlug);
                    sendResponse(exchange, 409, "Slug already in use.");
                }
            } else {
                String slug = urlService.shortenUrl(longUrl);
                response = "Short URL: http://localhost:8080/s/" + slug;
                logger.info("Anonymous short URL created: {}", response);
                sendResponse(exchange, 200, response);
            }

        } catch (Exception e) {
            logger.error("Exception while handling URL shortening", e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private String getLoggedInUser(HttpExchange exchange) {
        var cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("user=")) {
                    return cookie.substring("user=".length());
                }
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
