package com.serdyuchenko.urlshortcut.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Генерирует логины и пароли для зарегистрированных сайтов.
 */
@Component
public class CredentialGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int LOGIN_LENGTH = 12;

    private static final int PASSWORD_LENGTH = 16;

    private final SecureRandom random = new SecureRandom();

    /**
     * Генерирует случайный логин фиксированной длины.
     *
     * @return новый логин
     */
    public String newLogin() {
        return randomString(LOGIN_LENGTH);
    }

    /**
     * Генерирует случайный пароль фиксированной длины.
     *
     * @return новый пароль
     */
    public String newPassword() {
        return randomString(PASSWORD_LENGTH);
    }

    private String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
