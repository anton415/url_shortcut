package com.serdyuchenko.urlshortcut.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.serdyuchenko.urlshortcut.dto.StatisticResponse;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.model.Site;

@DataJpaTest
@DisplayName("Проверка атомарного счетчика shortcut")
class ShortcutStatisticRepositoryTest {

    @Autowired
    private ShortcutRepository shortcutRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Сущность по id возвращается для обновления и изменение total сохраняется")
    void whenFindEditableByIdThenCounterIsIncreasedInDatabase() {
        Shortcut shortcut = shortcutRepository.save(shortcut("job4j", "https://job4j.ru/resources/123", savedSite()));

        Shortcut editableShortcut = shortcutRepository.findEditableById(shortcut.getId()).orElseThrow();
        editableShortcut.setTotal(editableShortcut.getTotal() + 2);
        entityManager.flush();
        entityManager.clear();

        assertThat(shortcutRepository.findById(shortcut.getId()))
                .get()
                .extracting(Shortcut::getTotal)
                .isEqualTo(2L);
    }

    @Test
    @DisplayName("Статистика выбирается только по текущему сайту и сортируется по total")
    void whenFindStatisticsBySiteIdThenOnlySiteShortcutsAreReturnedInDeterministicOrder() {
        Site currentSite = savedSite("https://job4j.ru", "job4j-login", "job4j-password");
        Site anotherSite = savedSite("https://example.com", "example-login", "example-password");
        shortcutRepository.save(shortcut("low", "https://job4j.ru/resources/low", 1L, currentSite));
        shortcutRepository.save(shortcut("high", "https://job4j.ru/resources/high", 5L, currentSite));
        shortcutRepository.save(shortcut("foreign", "https://example.com/resources/foreign", 99L, anotherSite));

        List<StatisticResponse> statistics = shortcutRepository.findStatisticsBySiteId(currentSite.getId());

        assertThat(statistics)
                .hasSize(2)
                .satisfiesExactly(
                        first -> {
                            assertThat(first.getUrl()).isEqualTo("https://job4j.ru/resources/high");
                            assertThat(first.getTotal()).isEqualTo(5L);
                        },
                        second -> {
                            assertThat(second.getUrl()).isEqualTo("https://job4j.ru/resources/low");
                            assertThat(second.getTotal()).isEqualTo(1L);
                        }
                );
    }

    private Site savedSite() {
        return savedSite("https://job4j.ru", "job4j-login", "job4j-password");
    }

    private Site savedSite(String url, String login, String password) {
        Site site = new Site();
        site.setSite(url);
        site.setLogin(login);
        site.setPassword(password);
        return siteRepository.save(site);
    }

    private Shortcut shortcut(String code, String url, Site site) {
        return shortcut(code, url, 0L, site);
    }

    private Shortcut shortcut(String code, String url, long total, Site site) {
        Shortcut shortcut = new Shortcut();
        shortcut.setCode(code);
        shortcut.setUrl(url);
        shortcut.setTotal(total);
        shortcut.setSite(site);
        return shortcut;
    }
}
