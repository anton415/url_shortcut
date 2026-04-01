package com.serdyuchenko.urlshortcut.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.serdyuchenko.urlshortcut.dto.LoginRequest;
import com.serdyuchenko.urlshortcut.dto.TokenResponse;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.service.JwtService;
import com.serdyuchenko.urlshortcut.service.SiteAuthenticationService;

/**
 * REST-контроллер аутентификации сайтов.
 */
@RestController
public class AuthController {

    private final SiteAuthenticationService siteAuthenticationService;

    private final JwtService jwtService;

    public AuthController(SiteAuthenticationService siteAuthenticationService, JwtService jwtService) {
        this.siteAuthenticationService = siteAuthenticationService;
        this.jwtService = jwtService;
    }

    /**
     * Проверяет credentials сайта и возвращает JWT.
     *
     * @param request запрос с логином и паролем
     * @return JWT для последующих защищенных вызовов API
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Site site = siteAuthenticationService.authenticate(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(new TokenResponse(jwtService.generateToken(site)));
    }
}
