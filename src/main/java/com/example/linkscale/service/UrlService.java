
package com.example.linkscale.service;

import com.example.linkscale.dto.CreateUrlRequest;
import com.example.linkscale.dto.UrlResponse;
import com.example.linkscale.entity.ShortUrl;
import com.example.linkscale.repository.ShortUrlRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UrlService {

    private final ShortUrlRepository repository;

    public UrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request) {

        String originalUrl = request.originalUrl().trim();

        URI uri;
        try {
            uri = URI.create(originalUrl);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid URL");
        }

        String scheme = uri.getScheme();

        if (scheme == null ||
            !(scheme.equalsIgnoreCase("http")
                || scheme.equalsIgnoreCase("https")) ||
            uri.getHost() == null) {
            throw new IllegalArgumentException(
                "Only valid HTTP and HTTPS URLs are supported"
            );
        }

        String shortCode;

        // Generate a code that is not already in use.
        do {
            shortCode = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8);
        } while (repository.existsByShortCode(shortCode));

        ShortUrl saved = repository.save(
            new ShortUrl(originalUrl, shortCode)
        );

        return new UrlResponse(
            saved.getOriginalUrl(),
            saved.getShortCode(),
            "http://localhost:8080/" + saved.getShortCode()
        );
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortUrl::getOriginalUrl)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        NOT_FOUND, "Short URL not found"
                    )
                );
    }
}