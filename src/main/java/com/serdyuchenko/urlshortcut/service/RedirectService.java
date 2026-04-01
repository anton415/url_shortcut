package com.serdyuchenko.urlshortcut.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.serdyuchenko.urlshortcut.exception.ShortcutNotFoundException;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;

/**
 * Находит исходный URL по короткому коду и увеличивает счетчик переходов.
 */
@Service
public class RedirectService {

    private final ShortcutRepository shortcutRepository;

    public RedirectService(ShortcutRepository shortcutRepository) {
        this.shortcutRepository = shortcutRepository;
    }

    /**
     * Возвращает исходный URL и атомарно увеличивает счетчик переходов.
     *
     * @param code короткий код
     * @return исходный URL
     */
    @Transactional
    public String resolveUrl(String code) {
        Shortcut shortcut = shortcutRepository.findByCode(code)
                .orElseThrow(ShortcutNotFoundException::new);
        String url = shortcut.getUrl();
        int updatedRows = shortcutRepository.incrementTotalById(shortcut.getId());
        if (updatedRows != 1) {
            throw new IllegalStateException("Shortcut total was not updated");
        }
        return url;
    }
}
