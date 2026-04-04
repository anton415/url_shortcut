package com.serdyuchenko.urlshortcut.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.serdyuchenko.urlshortcut.dto.RegistrationResponse;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

/**
 * Сервис регистрации сайтов и выдачи учетных данных для API.
 */
@Service
public class SiteRegistrationService {

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
    public RegistrationResponse register(String rawSite) {
        String normalizedSite = siteNormalizer.normalize(rawSite);
        Site site = new Site();
        site.setSite(normalizedSite);
        site.setLogin(credentialGenerator.newLogin());
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

    private String generatePassword() {
        return credentialGenerator.newPassword();
    }
}
