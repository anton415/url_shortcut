package com.serdyuchenko.urlshortcut.security;

/**
 * Principal авторизованного сайта, извлеченный из JWT.
 */
public class AuthenticatedSite {

    private final Long siteId;

    private final String login;

    public AuthenticatedSite(Long siteId, String login) {
        this.siteId = siteId;
        this.login = login;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getLogin() {
        return login;
    }
}
