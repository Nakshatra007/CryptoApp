package com.example.cryptoapp;

public class Currency {
    private String name;
    private String iconUrl;
    private double exchangeRate;

    public Currency(String name, String iconUrl, double exchangeRate) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.exchangeRate = exchangeRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
