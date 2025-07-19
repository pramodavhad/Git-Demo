package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StaticResourceHandler implements HttpHandler {

    // Define clean URL aliases
    private static final Map<String, String> routeAliases = new HashMap<>() {{
        put("/", "/index.html");
        put("/login", "/login.html");
        put("/register", "/register.html");
        put("/myurls", "/myurls.html");
    }};

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();

            // Resolve alias if defined
            path = routeAliases.getOrDefault(path, path);

            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            InputStream is = getClass().getResourceAsStream("/static" + path);

            if (is == null) {
                String notFound = "404 Not Found";
                exchange.sendResponseHeaders(404, notFound.length());
                exchange.getResponseBody().write(notFound.getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().close();
                return;
            }

            String contentType = URLConnection.guessContentTypeFromName(path);
            if (contentType != null) {
                exchange.getResponseHeaders().add("Content-Type", contentType);
            }

            byte[] bytes = is.readAllBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (Exception ignored) {}
        }
    }
}
