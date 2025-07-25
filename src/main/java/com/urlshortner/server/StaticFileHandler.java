package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
	private final String filePath;

    public StaticFileHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String staticPath = "src/main/resources/static/" + filePath;
        File file = new File(staticPath);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String mimeType = Files.probeContentType(Paths.get(staticPath));
        byte[] bytes = Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set("Content-Type", mimeType != null ? mimeType : "application/octet-stream");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}

