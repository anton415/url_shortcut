package com.serdyuchenko.urlshortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serdyuchenko.urlshortcut.model.Shortcut;

/**
 * Репозиторий сокращенных ссылок.
 */
public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {

    /**
     * Проверяет занятость короткого кода.
     *
     * @param code код для проверки
     * @return {@code true}, если код уже существует
     */
    boolean existsByCode(String code);
}
