package com.serdyuchenko.urlshortcut.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serdyuchenko.urlshortcut.dto.StatisticResponse;
import com.serdyuchenko.urlshortcut.model.Shortcut;

/**
 * Репозиторий сокращенных ссылок.
 */
public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {

    /**
     * Ищет сокращенную ссылку по короткому коду.
     *
     * @param code короткий код
     * @return найденная ссылка, если код существует
     */
    Optional<Shortcut> findByCode(String code);

    /**
     * Проверяет занятость короткого кода.
     *
     * @param code код для проверки
     * @return {@code true}, если код уже существует
     */
    boolean existsByCode(String code);

    /**
     * Возвращает ссылку по идентификатору с блокировкой для безопасного обновления.
     *
     * @param shortcutId идентификатор сокращенной ссылки
     * @return найденная ссылка, если идентификатор существует
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT shortcut FROM Shortcut shortcut WHERE shortcut.id = :shortcutId")
    Optional<Shortcut> findEditableById(@Param("shortcutId") Long shortcutId);

    /**
     * Возвращает статистику ссылок конкретного сайта.
     *
     * @param siteId идентификатор сайта
     * @return список ссылок и общего числа переходов
     */
    @Query("""
            SELECT NEW com.serdyuchenko.urlshortcut.dto.StatisticResponse(shortcut.url, shortcut.total)
            FROM Shortcut shortcut
            WHERE shortcut.site.id = :siteId
            ORDER BY shortcut.total DESC, shortcut.url ASC
            """)
    List<StatisticResponse> findStatisticsBySiteId(@Param("siteId") Long siteId);
}
