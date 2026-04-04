package com.serdyuchenko.urlshortcut.service;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

/**
 * Создает сокращенные ссылки для авторизованных сайтов.
 */
@Service
public class ShortcutService {

    private static final int MAX_GENERATION_ATTEMPTS = 20;

    private final ShortcutRepository shortcutRepository;

    private final SiteRepository siteRepository;

    private final ShortcutCodeGenerator shortcutCodeGenerator;

    public ShortcutService(ShortcutRepository shortcutRepository,
                           SiteRepository siteRepository,
                           ShortcutCodeGenerator shortcutCodeGenerator) {
        this.shortcutRepository = shortcutRepository;
        this.siteRepository = siteRepository;
        this.shortcutCodeGenerator = shortcutCodeGenerator;
    }

    /**
     * Создает новую сокращенную ссылку для сайта.
     *
     * @param siteId идентификатор сайта из JWT
     * @param rawUrl исходный URL
     * @return сохраненная сокращенная ссылка
     */
    public Shortcut createShortcut(Long siteId, String rawUrl) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site does not exist"));
        String validatedUrl = validateUrl(rawUrl);
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            Shortcut shortcut = new Shortcut();
            shortcut.setSite(site);
            shortcut.setUrl(validatedUrl);
            shortcut.setCode(shortcutCodeGenerator.newCode());
            shortcut.setTotal(0L);
            try {
                return shortcutRepository.saveAndFlush(shortcut);
            } catch (DataIntegrityViolationException exception) {
                // Код уже занят, пробуем сгенерировать следующий.
            }
        }
        throw new IllegalStateException("Unable to generate unique shortcut code");
    }

    private String validateUrl(String rawUrl) {
        String candidate = rawUrl == null ? null : rawUrl.trim();
        if (candidate == null || candidate.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }
        try {
            URI uri = new URI(candidate);
            if (!uri.isAbsolute() || uri.getHost() == null || uri.getHost().isBlank()) {
                throw new IllegalArgumentException("URL must be absolute");
            }
            return candidate;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("URL must be valid", exception);
        }
    }
}
