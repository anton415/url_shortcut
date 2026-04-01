package com.serdyuchenko.urlshortcut.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.serdyuchenko.urlshortcut.dto.StatisticResponse;
import com.serdyuchenko.urlshortcut.security.AuthenticatedSite;
import com.serdyuchenko.urlshortcut.service.ShortcutStatisticService;

/**
 * REST-контроллер статистики по сокращенным ссылкам.
 */
@RestController
public class StatisticController {

    private final ShortcutStatisticService shortcutStatisticService;

    public StatisticController(ShortcutStatisticService shortcutStatisticService) {
        this.shortcutStatisticService = shortcutStatisticService;
    }

    /**
     * Возвращает статистику ссылок текущего авторизованного сайта.
     *
     * @param authentication principal текущего сайта
     * @return список DTO {url, total}
     */
    @GetMapping("/statistic")
    public ResponseEntity<List<StatisticResponse>> statistic(Authentication authentication) {
        AuthenticatedSite authenticatedSite = (AuthenticatedSite) authentication.getPrincipal();
        return ResponseEntity.ok(shortcutStatisticService.getStatistics(authenticatedSite.getSiteId()));
    }
}
