package com.serdyuchenko.urlshortcut.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serdyuchenko.urlshortcut.dto.RegistrationResponse;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

/**
 * Сервис регистрации сайтов и выдачи учетных данных для API.
 */
@Service
public class SiteRegistrationService {

    private static final int MAX_LOGIN_GENERATION_ATTEMPTS = 20;

    private final SiteRepository siteRepository;

    private final SiteNormalizer siteNormalizer;

    private final CredentialGenerator credentialGenerator;

    public SiteRegistrationService(SiteRepository siteRepository,
                                   SiteNormalizer siteNormalizer,
                                   CredentialGenerator credentialGenerator) {
        this.siteRepository = siteRepository;
        this.siteNormalizer = siteNormalizer;
        this.credentialGenerator = credentialGenerator;
    }

    /**
     * Регистрирует новый сайт, если он еще не существует в системе.
     *
     * @param rawSite адрес сайта в исходном виде
     * @return ответ с признаком регистрации и credentials при успехе
     */
    @Transactional
    public RegistrationResponse register(String rawSite) {
        String normalizedSite = siteNormalizer.normalize(rawSite);
        if (siteRepository.findBySite(normalizedSite).isPresent()) {
            return RegistrationResponse.alreadyRegistered();
        }
        Site site = new Site();
        site.setSite(normalizedSite);
        site.setLogin(generateUniqueLogin());
        site.setPassword(generatePassword());
        try {
            Site savedSite = siteRepository.saveAndFlush(site);
            return RegistrationResponse.registered(savedSite.getLogin(), savedSite.getPassword());
        } catch (DataIntegrityViolationException exception) {
            if (siteRepository.findBySite(normalizedSite).isPresent()) {
                return RegistrationResponse.alreadyRegistered();
            }
            throw exception;
        }
    }

    private String generateUniqueLogin() {
        for (int attempt = 0; attempt < MAX_LOGIN_GENERATION_ATTEMPTS; attempt++) {
            String login = credentialGenerator.newLogin();
            if (!siteRepository.existsByLogin(login)) {
                return login;
            }
        }
        throw new IllegalStateException("Unable to generate unique login");
    }

    private String generatePassword() {
        return credentialGenerator.newPassword();
    }
}
