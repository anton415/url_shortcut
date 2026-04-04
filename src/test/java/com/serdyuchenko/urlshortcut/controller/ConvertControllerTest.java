package com.serdyuchenko.urlshortcut.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.serdyuchenko.urlshortcut.dto.TokenResponse;
import com.serdyuchenko.urlshortcut.model.Shortcut;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;
import com.serdyuchenko.urlshortcut.service.ShortcutCodeGenerator;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверка защищенного convert")
class ConvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ShortcutRepository shortcutRepository;

    @MockBean
    private ShortcutCodeGenerator shortcutCodeGenerator;

    @BeforeEach
    void setUp() {
        shortcutRepository.deleteAll();
        siteRepository.deleteAll();
        when(shortcutCodeGenerator.newCode()).thenReturn("generated-code");
    }

    @Test
    @DisplayName("Без токена convert возвращает 401")
    void whenTokenIsMissingThenConvertReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://job4j.ru/resources/123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("С токеном сервис создает код и сохраняет shortcut за текущим сайтом")
    void whenTokenIsValidThenShortcutIsCreatedForAuthenticatedSite() throws Exception {
        Site savedSite = siteRepository.save(site("https://job4j.ru", "job4j-login", "job4j-password"));
        String token = loginAndGetToken(savedSite.getLogin(), savedSite.getPassword());

        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://job4j.ru/resources/123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty());

        assertThat(shortcutRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(shortcut -> {
                    assertThat(shortcut.getUrl()).isEqualTo("https://job4j.ru/resources/123");
                    assertThat(shortcut.getSite().getId()).isEqualTo(savedSite.getId());
                    assertThat(shortcut.getCode()).isEqualTo("generated-code");
                    assertThat(shortcut.getTotal()).isZero();
                });
    }

    @Test
    @DisplayName("При коллизии кода сервис повторяет генерацию и сохраняет shortcut со следующим кодом")
    void whenGeneratedCodeAlreadyExistsThenServiceRetriesSaveWithNewCode() throws Exception {
        Site savedSite = siteRepository.save(site("https://job4j.ru", "job4j-login", "job4j-password"));
        shortcutRepository.save(existingShortcut("duplicate-code", "https://job4j.ru/resources/existing", savedSite));
        when(shortcutCodeGenerator.newCode()).thenReturn("duplicate-code", "fresh-code");
        String token = loginAndGetToken(savedSite.getLogin(), savedSite.getPassword());

        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://job4j.ru/resources/123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("fresh-code"));

        assertThat(shortcutRepository.findAll())
                .extracting(Shortcut::getCode)
                .containsExactlyInAnyOrder("duplicate-code", "fresh-code");
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

    private Shortcut existingShortcut(String code, String url, Site site) {
        Shortcut shortcut = new Shortcut();
        shortcut.setCode(code);
        shortcut.setUrl(url);
        shortcut.setTotal(0L);
        shortcut.setSite(site);
        return shortcut;
    }
}
