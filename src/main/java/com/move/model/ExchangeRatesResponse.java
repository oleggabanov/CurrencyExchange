package com.move.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ExchangeRatesResponse {

  private int id;
  private String baseCurrencyId;
  private String targetCurrencyId;
  private BigDecimal rate;

}
