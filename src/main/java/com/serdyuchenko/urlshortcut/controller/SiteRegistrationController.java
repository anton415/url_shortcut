package com.serdyuchenko.urlshortcut.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.serdyuchenko.urlshortcut.dto.RegistrationRequest;
import com.serdyuchenko.urlshortcut.dto.RegistrationResponse;
import com.serdyuchenko.urlshortcut.service.SiteRegistrationService;

/**
 * REST-контроллер для регистрации сайтов в системе.
 */
@RestController
public class SiteRegistrationController {

    private final SiteRegistrationService siteRegistrationService;

    public SiteRegistrationController(SiteRegistrationService siteRegistrationService) {
        this.siteRegistrationService = siteRegistrationService;
    }

    /**
     * Регистрирует сайт и возвращает учетные данные для дальнейшей работы с API.
     *
     * @param request запрос с адресом сайта
     * @return результат регистрации и сгенерированные credentials при успехе
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(siteRegistrationService.register(request.getSite()));
    }
}
