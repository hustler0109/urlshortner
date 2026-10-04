
package com.example.linkscale.dto;

public record UrlResponse(
    String originalUrl,
    String shortCode,
    String shortUrl
) {}