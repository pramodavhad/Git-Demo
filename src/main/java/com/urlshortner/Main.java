package com.urlshortner;

import com.urlshortner.server.LoginHandler;
import com.urlshortner.server.MyUrlsHandler;
import com.urlshortner.server.RedirectHandler;
import com.urlshortner.server.RegisterHandler;
import com.urlshortner.server.StaticResourceHandler;
//import com.urlshortner.server.StaticFileHandler;
import com.urlshortner.server.UrlHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Main {
	
	public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/shorten", new UrlHandler());
        server.createContext("/s", new RedirectHandler());
        server.createContext("/register", new RegisterHandler());
        server.createContext("/login", new LoginHandler());
		server.createContext("/myurls", new MyUrlsHandler());
	
		server.createContext("/", new StaticResourceHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }
	
//	public static void main(String[] args) throws IOException {
//	    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//
//	    server.createContext("/shorten", new UrlHandler());
//	    server.createContext("/s", new RedirectHandler());
//	    server.createContext("/register", new RegisterHandler());
//	    server.createContext("/login", new LoginHandler());
//	    server.createContext("/myurls", new MyUrlsHandler());
//	    
//	 // Static file handler (HTML, CSS, etc.)
//	    server.createContext("/", new StaticFileHandler("src/main/resources/static"));
//
//	    server.setExecutor(null);
//	    server.start();
//
//	    System.out.println("Server started at http://localhost:8080");
//	}

}
