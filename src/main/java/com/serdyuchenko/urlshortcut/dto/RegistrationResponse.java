package com.serdyuchenko.urlshortcut.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO ответа на регистрацию сайта.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponse {

    private boolean registration;

    private String login;

    private String password;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean registration, String login, String password) {
        this.registration = registration;
        this.login = login;
        this.password = password;
    }

    /**
     * Создает ответ для успешной регистрации.
     *
     * @param login сгенерированный логин сайта
     * @param password сгенерированный пароль сайта
     * @return DTO с признаком успешной регистрации и учетными данными
     */
    public static RegistrationResponse registered(String login, String password) {
        return new RegistrationResponse(true, login, password);
    }

    /**
     * Создает ответ для случая, когда сайт уже зарегистрирован.
     *
     * @return DTO с признаком неуспешной регистрации без учетных данных
     */
    public static RegistrationResponse alreadyRegistered() {
        return new RegistrationResponse(false, null, null);
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
