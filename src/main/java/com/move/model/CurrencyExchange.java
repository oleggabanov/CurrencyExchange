package com.move.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyExchange {

  Currency baseCurrency;
  Currency targetCurrency;
  BigDecimal rate;
  BigDecimal amount;
  BigDecimal convertedAmount;

}
