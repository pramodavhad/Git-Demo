package com.urlshortner.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static final String DB_URL = "jdbc:h2:~/urlshortener";
    private final Connection conn;

    public Database() {
        try {
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS urls (
                        slug VARCHAR(10) PRIMARY KEY,
                        long_url VARCHAR(2048),
                        username VARCHAR(50)
                    );
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username VARCHAR(50) PRIMARY KEY,
                        password_hash VARCHAR(256)
                    );
                """);

                logger.info("Database initialized successfully");
            }
        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException(e);
        }
    }

    public boolean save(String slug, String url, String user) {
        String query = "INSERT INTO urls (slug, long_url, username) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, slug);
            ps.setString(2, url);
            ps.setString(3, user);
            ps.executeUpdate();
            logger.info("Inserted URL: slug='{}', user='{}', url='{}'", slug, user, url);
            return true;
        } catch (SQLException e) {
            logger.warn("Failed to insert URL: slug='{}' may already exist", slug, e);
            return false; // Likely slug conflict
        }
    }

    public String findBySlug(String slug) {
        String query = "SELECT long_url FROM urls WHERE slug = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, slug);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String url = rs.getString("long_url");
                logger.info("Found URL for slug '{}': {}", slug, url);
                return url;
            } else {
                logger.warn("Slug not found: '{}'", slug);
            }
        } catch (SQLException e) {
            logger.error("Error looking up slug '{}'", slug, e);
        }
        return null;
    }

    public boolean saveUser(String username, String hash) {
        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.executeUpdate();
            logger.info("User registered successfully: '{}'", username);
            return true;
        } catch (SQLException e) {
            logger.warn("Failed to register user '{}': possibly already exists", username, e);
            return false;
        }
    }

    public String getPasswordHash(String username) {
        String query = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                logger.info("Password hash retrieved for user '{}'", username);
                return hash;
            } else {
                logger.warn("User not found when retrieving password hash: '{}'", username);
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve password hash for '{}'", username, e);
        }
        return null;
    }

    public List<Map<String, String>> getUrlsByUser(String username) {
        String query = "SELECT slug, long_url FROM urls WHERE username = ?";
        List<Map<String, String>> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> entry = new HashMap<>();
                entry.put("slug", rs.getString("slug"));
                entry.put("longUrl", rs.getString("long_url"));
                results.add(entry);
            }
            logger.info("Fetched {} URLs for user '{}'", results.size(), username);
        } catch (SQLException e) {
            logger.error("Failed to retrieve URLs for user '{}'", username, e);
        }
        return results;
    }
}
