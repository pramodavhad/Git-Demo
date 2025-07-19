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
        HttpExchange exchange = mock(HttpExchange.class);

        // Simulate POST request
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/shorten"));

        String input = "longUrl=https%3A%2F%2Fexample.com";
        InputStream requestBody = new ByteArrayInputStream(input.getBytes());
        when(exchange.getRequestBody()).thenReturn(requestBody);

        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        UrlHandler handler = new UrlHandler();
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}

