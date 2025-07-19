package com.urlshortner.service;

import java.util.UUID;

import com.urlshortner.db.Database;

public class UrlService {
	
	private final Database db = new Database();

	public String shortenUrl(String longUrl) {
	    String slug = UUID.randomUUID().toString().substring(0, 6);
	    db.save(slug, longUrl, null); // anonymous
	    return slug;
	}

    public String getOriginalUrl(String slug) {
        return db.findBySlug(slug);
    }
    
    public boolean shortenUrlWithCustomSlug(String longUrl, String customSlug, String username) {
        return db.save(customSlug, longUrl, username);
    }

}
