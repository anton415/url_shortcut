package com.serdyuchenko.urlshortcut.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.serdyuchenko.urlshortcut.dto.TokenResponse;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверка статистики ссылок")
class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Без токена statistic возвращает 401")
    void whenTokenIsMissingThenStatisticReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/statistic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("С токеном statistic возвращает только ссылки текущего сайта")
    void whenTokenIsValidThenStatisticContainsOnlyCurrentSiteShortcuts() throws Exception {
        Site currentSite = siteRepository.save(site("https://job4j.ru", "job4j-login", "job4j-password"));
        Site anotherSite = siteRepository.save(site("https://example.com", "example-login", "example-password"));
        shortcutRepository.save(shortcut("high", "https://job4j.ru/resources/high", 7L, currentSite));
        shortcutRepository.save(shortcut("low", "https://job4j.ru/resources/low", 2L, currentSite));
        shortcutRepository.save(shortcut("foreign", "https://example.com/resources/foreign", 99L, anotherSite));
        String token = loginAndGetToken(currentSite.getLogin(), currentSite.getPassword());

        mockMvc.perform(get("/statistic")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].url").value("https://job4j.ru/resources/high"))
                .andExpect(jsonPath("$[0].total").value(7))
                .andExpect(jsonPath("$[1].url").value("https://job4j.ru/resources/low"))
                .andExpect(jsonPath("$[1].total").value(2));
    }

    private String loginAndGetToken(String login, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class).getToken();
    }

    private Site site(String url, String login, String password) {
        Site site = new Site();
        site.setSite(url);
        site.setLogin(login);
        site.setPassword(password);
        return site;
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
