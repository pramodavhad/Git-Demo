package com.urlshortner;

import org.junit.jupiter.api.*;

import com.urlshortner.db.Database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class DatabaseTest {
	private static Database db;

    @BeforeAll
    public static void setup() {
        db = new Database(); // assumes in-memory H2 or ~/urlshortener
    }

    @Test
    public void testInsertAndRetrieveUrl() {
        String longUrl = "https://example.com";
        String slug = UUID.randomUUID().toString().substring(0, 8); // generate a test slug

        boolean saved = db.save(slug, longUrl, null); // anonymous user
        assertTrue(saved, "URL should be saved successfully");

        String retrieved = db.findBySlug(slug);
        assertEquals(longUrl, retrieved);
    }

    @Test
    public void testUserRegistrationAndLogin() {
        String username = "testuser";
        String passwordHash = "hashedPassword123";

        boolean registered = db.saveUser(username, passwordHash);
        assertTrue(registered || passwordAlreadyExists(username), "User registration should succeed or user already exists");

        String retrievedHash = db.getPasswordHash(username);
        assertEquals(passwordHash, retrievedHash);
    }

    private boolean passwordAlreadyExists(String username) {
        return db.getPasswordHash(username) != null;
    }
    
}
