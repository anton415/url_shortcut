package com.serdyuchenko.urlshortcut.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.serdyuchenko.urlshortcut.dto.ConvertRequest;
import com.serdyuchenko.urlshortcut.dto.ConvertResponse;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.security.AuthenticatedSite;
import com.serdyuchenko.urlshortcut.service.ShortcutService;

/**
 * REST-контроллер сокращения ссылок.
 */
@RestController
public class ConvertController {

    private final ShortcutService shortcutService;

    public ConvertController(ShortcutService shortcutService) {
        this.shortcutService = shortcutService;
    }

    /**
     * Создает короткий код для исходного URL текущего сайта.
     *
     * @param request запрос с URL
     * @param authentication principal текущего сайта
     * @return DTO с выданным коротким кодом
     */
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> convert(@Valid @RequestBody ConvertRequest request,
                                                   Authentication authentication) {
        AuthenticatedSite authenticatedSite = (AuthenticatedSite) authentication.getPrincipal();
        Shortcut shortcut = shortcutService.createShortcut(authenticatedSite.getSiteId(), request.getUrl());
        return ResponseEntity.ok(new ConvertResponse(shortcut.getCode()));
    }
}
