package com.serdyuchenko.urlshortcut.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO запроса на сокращение URL.
 */
public class ConvertRequest {

    @NotBlank
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
