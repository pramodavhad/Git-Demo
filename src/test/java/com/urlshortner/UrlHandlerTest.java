package com.urlshortner;

import com.sun.net.httpserver.*;
import com.urlshortner.server.UrlHandler;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.URI;

import static org.mockito.Mockito.*;

public class UrlHandlerTest {

    @Test
    public void testHandlePostWithValidData() throws IOException {
        // Create a mock HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Simulate POST request
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/shorten"));

        // Simulate request body
        String input = "longUrl=https%3A%2F%2Fexample.com";
        InputStream requestBody = new ByteArrayInputStream(input.getBytes());
        when(exchange.getRequestBody()).thenReturn(requestBody);

        // Mock response headers
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);

        // Mock response body
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        // Execute handler
        UrlHandler handler = new UrlHandler();
        handler.handle(exchange);

        // Verify status code sent
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}


