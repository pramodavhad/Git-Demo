package com.urlshortner.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.urlshortner.db.Database;

public class AuthService {
	
	private final Database db = new Database();

    public boolean register(String username, String password) {
        String hash = hashPassword(password);
        return db.saveUser(username, hash);
    }

    public boolean login(String username, String password) {
        String storedHash = db.getPasswordHash(username);
        return storedHash != null && storedHash.equals(hashPassword(password));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available");
        }
    }

}
