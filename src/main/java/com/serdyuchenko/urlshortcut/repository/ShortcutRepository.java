package com.serdyuchenko.urlshortcut.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
     * Атомарно увеличивает счетчик переходов по ссылке.
     *
     * @param shortcutId идентификатор сокращенной ссылки
     * @return количество обновленных строк
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Shortcut shortcut set shortcut.total = shortcut.total + 1 where shortcut.id = :shortcutId")
    int incrementTotalById(@Param("shortcutId") Long shortcutId);

    /**
     * Возвращает статистику ссылок конкретного сайта.
     *
     * @param siteId идентификатор сайта
     * @return список ссылок и общего числа переходов
     */
    @Query("""
            select new com.serdyuchenko.urlshortcut.dto.StatisticResponse(shortcut.url, shortcut.total)
            from Shortcut shortcut
            where shortcut.site.id = :siteId
            order by shortcut.total desc, shortcut.url asc
            """)
    List<StatisticResponse> findStatisticsBySiteId(@Param("siteId") Long siteId);
}
