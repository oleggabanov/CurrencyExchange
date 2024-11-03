package com.move.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ExchangeRateResponse {

  private int id;
  private CurrencyResponse baseCurrency;
  private CurrencyResponse targetCurrency;
  private BigDecimal rate;

}
