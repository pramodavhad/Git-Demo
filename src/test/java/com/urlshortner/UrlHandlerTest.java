package com.urlshortner;

import com.sun.net.httpserver.*;
import com.urlshortner.server.UrlHandler;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UrlHandlerTest {

	@Test
	public void testHandlePostWithValidData() throws IOException {
		// Mock HttpExchange
		HttpExchange exchange = mock(HttpExchange.class);

		// Request setup
		when(exchange.getRequestMethod()).thenReturn("POST");
		when(exchange.getRequestURI()).thenReturn(URI.create("/shorten"));

		// Input should use correct field name: url=
		String input = "url=https%3A%2F%2Fexample.com"; // Proper key expected by UrlHandler
		InputStream requestBody = new ByteArrayInputStream(input.getBytes());
		when(exchange.getRequestBody()).thenReturn(requestBody);

		// Mock headers
		Headers requestHeaders = new Headers();
		when(exchange.getRequestHeaders()).thenReturn(requestHeaders); // Empty header = unauthenticated
		Headers responseHeaders = new Headers();
		when(exchange.getResponseHeaders()).thenReturn(responseHeaders);

		// Output
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(exchange.getResponseBody()).thenReturn(outputStream);

		// Handle request
		UrlHandler handler = new UrlHandler();
		handler.handle(exchange);

		// Verify response
		verify(exchange).sendResponseHeaders(eq(200), anyLong());

		String response = outputStream.toString();
		System.out.println("Response: " + response);
		assertTrue(response.contains("Short URL: http://localhost:8080/s/"));
	}
}
