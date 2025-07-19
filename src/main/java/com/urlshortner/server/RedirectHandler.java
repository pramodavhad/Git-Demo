package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortner.service.UrlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class RedirectHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedirectHandler.class);
    private final UrlService urlService = new UrlService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            URI uri = exchange.getRequestURI(); // Example: /s/abc123
            String path = uri.getPath();

            if (!path.startsWith("/s/") || path.length() <= 3) {
                logger.warn("Malformed URL path: {}", path);
                sendResponse(exchange, 400, "400 - Bad Request");
                return;
            }

            String slug = path.substring("/s/".length());
            logger.info("Handling redirect for slug: {}", slug);

            String originalUrl = urlService.getOriginalUrl(slug);

            if (originalUrl != null) {
                logger.info("Redirecting slug '{}' to '{}'", slug, originalUrl);
                exchange.getResponseHeaders().add("Location", originalUrl);
                exchange.sendResponseHeaders(302, -1); // 302 Found redirect
            } else {
                logger.warn("Slug not found: {}", slug);
                sendResponse(exchange, 404, "404 - URL not found");
            }

        } catch (Exception e) {
            logger.error("Exception during redirect", e);
            sendResponse(exchange, 500, "500 - Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}
