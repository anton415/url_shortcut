package com.serdyuchenko.urlshortcut.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.serdyuchenko.urlshortcut.repository.ShortcutRepository;
import com.serdyuchenko.urlshortcut.repository.SiteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверка регистрации сайта")
class SiteRegistrationControllerTest {

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
    @DisplayName("Новый сайт регистрируется и сохраняется в базе данных")
    void whenRegisterNewSiteThenCredentialsAreReturnedAndSiteIsStored() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"https://job4j.ru/\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(true))
                .andExpect(jsonPath("$.login").isNotEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty());

        assertThat(siteRepository.findAll())
                .hasSize(1)
                .first()
                .extracting("site")
                .isEqualTo("https://job4j.ru");
    }

    @Test
    @DisplayName("Повторная регистрация того же сайта отклоняется без создания дубликата")
    void whenRegisterExistingSiteThenRegistrationIsRejectedWithoutDuplicate() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"https://job4j.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(true));

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"https://job4j.ru/\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(false))
                .andExpect(jsonPath("$.login").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        assertThat(siteRepository.count()).isEqualTo(1);
    }
}
