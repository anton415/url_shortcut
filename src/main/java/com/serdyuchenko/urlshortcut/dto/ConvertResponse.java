package com.serdyuchenko.urlshortcut.dto;

/**
 * DTO ответа с коротким кодом.
 */
public class ConvertResponse {

    private String code;

    public ConvertResponse() {
    }

    public ConvertResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
