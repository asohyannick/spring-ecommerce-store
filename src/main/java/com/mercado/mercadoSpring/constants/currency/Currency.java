package com.mercado.mercadoSpring.constants.currency;
public enum Currency {
    USD,
    EUR,
    GBP,
    JPY,
    AUD,
    CAD,
    CHF,
    CNY,
    SEK,
    NZD,
    MXN,
    SGD,
    HKD,
    NOK,
    KRW,
    TRY,
    RUB,
    INR,
    BRL,
    ZAR,
    ARS,
    COP,
    CLP,
    PEN,
    VND;
    public String StripeCode() {
        return this.name().toLowerCase().trim();
    }
}
