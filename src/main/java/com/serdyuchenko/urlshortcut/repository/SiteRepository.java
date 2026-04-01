package com.serdyuchenko.urlshortcut.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.serdyuchenko.urlshortcut.model.Site;

/**
 * Репозиторий для работы с зарегистрированными сайтами.
 */
public interface SiteRepository extends JpaRepository<Site, Long> {

    /**
     * Ищет сайт по нормализованному адресу.
     *
     * @param site нормализованный адрес сайта
     * @return найденный сайт или пустой результат
     */
    Optional<Site> findBySite(String site);

    /**
     * Проверяет, занят ли логин другим сайтом.
     *
     * @param login логин для проверки
     * @return {@code true}, если логин уже существует
     */
    boolean existsByLogin(String login);

    /**
     * Проверяет, занят ли пароль другим сайтом.
     *
     * @param password пароль для проверки
     * @return {@code true}, если пароль уже существует
     */
    boolean existsByPassword(String password);
}
