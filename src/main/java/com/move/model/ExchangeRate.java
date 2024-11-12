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
  private Currency baseCurrency;
  private Currency targetCurrency;
  private BigDecimal rate;

}
