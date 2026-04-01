package com.serdyuchenko.urlshortcut.exception;

/**
 * Ошибка отсутствующей сокращенной ссылки.
 */
public class ShortcutNotFoundException extends RuntimeException {

    public ShortcutNotFoundException() {
        super("Shortcut not found");
    }
}
