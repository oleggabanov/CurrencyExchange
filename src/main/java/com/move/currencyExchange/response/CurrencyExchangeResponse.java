package com.move.currencyExchange.response;


import com.move.currency.response.CurrencyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyExchangeResponse {

  CurrencyResponse baseCurrency;
  CurrencyResponse targetCurrency;
  BigDecimal rate;
  BigDecimal amount;
  BigDecimal convertedAmount;

}
