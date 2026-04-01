package com.serdyuchenko.urlshortcut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение сервиса сокращения ссылок.
 */
@SpringBootApplication
public class UrlShortcutApplication {

    /**
     * Запускает Spring Boot приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(UrlShortcutApplication.class, args);
    }
}
