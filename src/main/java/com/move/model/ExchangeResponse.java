package com.move.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeResponse {

  private int id;
  private String baseCurrencyId;
  private String targetCurrencyId;
  private BigDecimal rate;

}
