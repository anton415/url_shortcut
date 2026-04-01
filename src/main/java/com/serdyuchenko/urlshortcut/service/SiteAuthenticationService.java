package com.serdyuchenko.urlshortcut.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

/**
 * Проверяет credentials сайта перед выдачей JWT.
 */
@Service
public class SiteAuthenticationService {

    private final SiteRepository siteRepository;

    public SiteAuthenticationService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    /**
     * Аутентифицирует сайт по логину и паролю.
     *
     * @param login логин API-клиента
     * @param password пароль API-клиента
     * @return найденный сайт
     */
    public Site authenticate(String login, String password) {
        return siteRepository.findByLoginAndPassword(login, password)
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));
    }
}
