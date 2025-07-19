package com.urlshortner.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticFolderHandler implements HttpHandler {
    private final String baseFolder;

    public StaticFolderHandler(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        String filePath = "src/main/resources/static" + uriPath;

        File file = new File(filePath);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String mimeType = Files.probeContentType(Paths.get(filePath));
        byte[] bytes = Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set("Content-Type", mimeType != null ? mimeType : "application/octet-stream");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
