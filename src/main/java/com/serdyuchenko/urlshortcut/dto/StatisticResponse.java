package com.serdyuchenko.urlshortcut.dto;

/**
 * DTO строки статистики по сокращенной ссылке.
 */
public class StatisticResponse {

    private String url;

    private long total;

    public StatisticResponse() {
    }

    public StatisticResponse(String url, long total) {
        this.url = url;
        this.total = total;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
