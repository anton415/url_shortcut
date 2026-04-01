package com.serdyuchenko.urlshortcut.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Приводит адрес сайта к каноническому виду для хранения и сравнения.
 */
@Component
public class SiteNormalizer {

    /**
     * Нормализует адрес сайта, приводя схему и host к нижнему регистру
     * и отбрасывая путь, query и fragment.
     *
     * @param rawSite адрес сайта из пользовательского запроса
     * @return нормализованный адрес вида {@code scheme://host[:port]}
     */
    public String normalize(String rawSite) {
        if (rawSite == null || rawSite.isBlank()) {
            throw new IllegalArgumentException("Site must not be blank");
        }
        String candidate = rawSite.trim();
        if (!candidate.contains("://")) {
            candidate = "https://" + candidate;
        }
        try {
            URI uri = new URI(candidate);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null || host.isBlank()) {
                throw new IllegalArgumentException("Site must be a valid URL");
            }
            String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
            String normalizedHost = host.toLowerCase(Locale.ROOT);
            int port = uri.getPort();
            return port > -1
                    ? normalizedScheme + "://" + normalizedHost + ":" + port
                    : normalizedScheme + "://" + normalizedHost;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Site must be a valid URL", e);
        }
    }
}
