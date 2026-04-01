package com.serdyuchenko.urlshortcut.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверка публичного redirect")
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ShortcutRepository shortcutRepository;

    @BeforeEach
    void setUp() {
        shortcutRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    @DisplayName("Существующий код возвращает 302 и увеличивает total")
    void whenCodeExistsThenRedirectIsReturnedAndTotalIsIncremented() throws Exception {
        Shortcut shortcut = shortcutRepository.save(shortcut("job4j", "https://job4j.ru/resources/123", savedSite()));

        mockMvc.perform(get("/redirect/{code}", shortcut.getCode()))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://job4j.ru/resources/123"));

        assertThat(shortcutRepository.findById(shortcut.getId()))
                .get()
                .extracting(Shortcut::getTotal)
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("Неизвестный код возвращает 404")
    void whenCodeDoesNotExistThenNotFoundIsReturned() throws Exception {
        mockMvc.perform(get("/redirect/{code}", "missing-code"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Shortcut not found"));
    }

    private Site savedSite() {
        Site site = new Site();
        site.setSite("https://job4j.ru");
        site.setLogin("job4j-login");
        site.setPassword("job4j-password");
        return siteRepository.save(site);
    }

    private Shortcut shortcut(String code, String url, Site site) {
        Shortcut shortcut = new Shortcut();
        shortcut.setCode(code);
        shortcut.setUrl(url);
        shortcut.setTotal(0L);
        shortcut.setSite(site);
        return shortcut;
    }
}
