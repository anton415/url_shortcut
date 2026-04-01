package com.serdyuchenko.urlshortcut.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO запроса на регистрацию сайта.
 */
public class RegistrationRequest {

    @NotBlank
    private String site;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
