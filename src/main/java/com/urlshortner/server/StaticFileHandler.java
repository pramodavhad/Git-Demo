//package com.urlshortner.server;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//
//import java.io.*;
//import java.nio.file.Files;

//public class StaticFileHandler implements HttpHandler {
//    private final String basePath;
//
//    public StaticFileHandler(String basePath) {
//        this.basePath = basePath;
//    }
//
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        String uriPath = exchange.getRequestURI().getPath();
//
//        // default to index.html
//        if (uriPath.equals("/")) {
//            uriPath = "/index.html";
//        }
//
//        File file = new File(basePath + uriPath);
//        if (!file.exists() || file.isDirectory()) {
//            String response = "404 Not Found: " + uriPath;
//            exchange.sendResponseHeaders(404, response.length());
//            OutputStream os = exchange.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//            return;
//        }
//
//        // Set content type based on file extension
//        String contentType = guessContentType(file.getName());
//        exchange.getResponseHeaders().set("Content-Type", contentType);
//
//        byte[] bytes = Files.readAllBytes(file.toPath());
//        exchange.sendResponseHeaders(200, bytes.length);
//        OutputStream os = exchange.getResponseBody();
//        os.write(bytes);
//        os.close();
//    }
//
//    private String guessContentType(String filename) {
//        if (filename.endsWith(".html")) return "text/html";
//        if (filename.endsWith(".css")) return "text/css";
//        if (filename.endsWith(".js")) return "application/javascript";
//        return "application/octet-stream";
//    }
//}

