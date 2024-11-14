package com.move.dto;


import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ExchangeRateDto(String baseCurrencyCode,String targetCurrencyCode, BigDecimal rate) {
}
