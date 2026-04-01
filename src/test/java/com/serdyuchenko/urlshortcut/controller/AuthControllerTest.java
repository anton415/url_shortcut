package com.serdyuchenko.urlshortcut.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверка аутентификации сайта")
class AuthControllerTest {

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
    @DisplayName("Валидные credentials возвращают JWT")
    void whenCredentialsAreValidThenJwtIsReturned() throws Exception {
        siteRepository.save(site("https://job4j.ru", "job4j-login", "job4j-password"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"job4j-login\",\"password\":\"job4j-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Неверные credentials возвращают 401")
    void whenCredentialsAreInvalidThenUnauthorizedIsReturned() throws Exception {
        siteRepository.save(site("https://job4j.ru", "job4j-login", "job4j-password"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"job4j-login\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login or password"));
    }

    private Site site(String url, String login, String password) {
        Site site = new Site();
        site.setSite(url);
        site.setLogin(login);
        site.setPassword(password);
        return site;
    }
}
