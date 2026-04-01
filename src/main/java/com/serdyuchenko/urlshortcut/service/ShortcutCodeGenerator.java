package com.serdyuchenko.urlshortcut.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Генерирует короткие коды для сокращенных ссылок.
 */
@Component
public class ShortcutCodeGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    /**
     * Возвращает новый случайный код фиксированной длины.
     *
     * @return короткий код
     */
    public String newCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
