package com.urlshortner.server;

import com.urlshortner.db.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MyUrlsHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyUrlsHandler.class);
    private final Database db = new Database();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String user = getLoggedInUser(exchange);

        if (user == null) {
            logger.warn("Unauthorized access attempt to /my-urls from {}", exchange.getRemoteAddress());
            sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        try {
            logger.info("Fetching URLs for user '{}'", user);
            List<Map<String, String>> urls = db.getUrlsByUser(user);

            JSONArray json = new JSONArray();
            for (Map<String, String> entry : urls) {
                JSONObject obj = new JSONObject();
                obj.put("slug", entry.get("slug"));
                obj.put("longUrl", entry.get("longUrl"));
                json.put(obj);
            }

            byte[] responseBytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

            logger.info("Returned {} URLs for user '{}'", urls.size(), user);

        } catch (Exception e) {
            logger.error("Error retrieving URLs for user '{}'", user, e);
            sendResponse(exchange, 500, "Internal server error");
        }
    }

    private String getLoggedInUser(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
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
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
