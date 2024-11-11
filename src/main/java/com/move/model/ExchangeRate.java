package com.move.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ExchangeRate {

  private int id;
  private CurrencyResponse baseCurrency;
  private CurrencyResponse targetCurrency;
  private BigDecimal rate;

}
