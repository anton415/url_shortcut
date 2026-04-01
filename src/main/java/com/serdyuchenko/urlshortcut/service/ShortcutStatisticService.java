package com.serdyuchenko.urlshortcut.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.serdyuchenko.urlshortcut.dto.StatisticResponse;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;

/**
 * Возвращает статистику переходов по ссылкам сайта.
 */
@Service
public class ShortcutStatisticService {

    private final ShortcutRepository shortcutRepository;

    public ShortcutStatisticService(ShortcutRepository shortcutRepository) {
        this.shortcutRepository = shortcutRepository;
    }

    /**
     * Возвращает статистику только по ссылкам текущего сайта.
     *
     * @param siteId идентификатор сайта из JWT
     * @return список ссылок и количества переходов
     */
    @Transactional(readOnly = true)
    public List<StatisticResponse> getStatistics(Long siteId) {
        return shortcutRepository.findStatisticsBySiteId(siteId);
    }
}
