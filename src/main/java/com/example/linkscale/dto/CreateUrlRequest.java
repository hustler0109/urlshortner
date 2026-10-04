
package com.example.linkscale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateUrlRequest(
    @NotBlank(message = "URL is required")
    @URL(message = "Enter a valid URL")
    @Size(max = 2048, message = "URL is too long")
    String originalUrl
) {}