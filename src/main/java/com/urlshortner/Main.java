package com.urlshortner;

import com.urlshortner.server.LoginHandler;
import com.urlshortner.server.MyUrlsHandler;
import com.urlshortner.server.RedirectHandler;
import com.urlshortner.server.RegisterHandler;
import com.urlshortner.server.StaticFileHandler;
import com.urlshortner.server.StaticFolderHandler;
import com.urlshortner.server.UrlHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		 HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

		 // Static file handler for /, /index, /login, /register, etc.
	        server.createContext("/", new StaticFileHandler("index.html"));
	        server.createContext("/index", new StaticFileHandler("index.html"));
	        server.createContext("/login", new StaticFileHandler("login.html"));
	        server.createContext("/register", new StaticFileHandler("register.html"));

	        // Static folder handler for JS, CSS, and other assets
	        server.createContext("/css", new StaticFolderHandler("css"));
	        server.createContext("/js", new StaticFolderHandler("js"));

	        // API endpoints
	        server.createContext("/shorten", new UrlHandler());
	        server.createContext("/s", new RedirectHandler());
	        server.createContext("/registerUser", new RegisterHandler()); 
	        server.createContext("/loginUser", new LoginHandler());       
	        server.createContext("/myurls", new MyUrlsHandler());

	        server.setExecutor(null);
	        server.start();
	        System.out.println("Server started at http://localhost:8080");
    }

}
