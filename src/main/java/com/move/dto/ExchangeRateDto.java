package com.move.dto;


import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ExchangeRateDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
}
