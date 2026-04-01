package com.serdyuchenko.urlshortcut.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.serdyuchenko.urlshortcut.service.RedirectService;

/**
 * Публичный redirect по короткому коду.
 */
@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    /**
     * Перенаправляет клиента на исходный URL по короткому коду.
     *
     * @param code короткий код ссылки
     * @return ответ 302 с заголовком Location
     */
    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String url = redirectService.resolveUrl(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}
