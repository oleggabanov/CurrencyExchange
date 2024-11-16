package com.move.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CurrencyExchangeDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
}
