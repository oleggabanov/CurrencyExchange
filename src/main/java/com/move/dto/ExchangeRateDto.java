package com.move.dto;


import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ExchangeRateDto(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
}
