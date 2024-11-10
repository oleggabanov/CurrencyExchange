package com.move.service;

import com.move.model.CurrencyExchangeResponse;
import com.move.model.ExchangeRateResponse;
import lombok.SneakyThrows;

import java.math.BigDecimal;

public class CurrencyExchangeService {

  private ExchangeRatesService exchangeRatesService;

  public CurrencyExchangeService() {
    this.exchangeRatesService = new ExchangeRatesService();
  }

  @SneakyThrows
  public CurrencyExchangeResponse convertCurrency(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
    ExchangeRateResponse exchangeRateResponse = exchangeRatesService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
    BigDecimal rate = exchangeRateResponse.getRate();
    BigDecimal convertedAmount = amount.multiply(rate);

    CurrencyExchangeResponse currencyExchangeResponse = CurrencyExchangeResponse.builder()
            .baseCurrency(exchangeRateResponse.getBaseCurrency())
            .targetCurrency(exchangeRateResponse.getTargetCurrency())
            .rate(rate)
            .amount(amount)
            .convertedAmount(convertedAmount)
            .build();

    return currencyExchangeResponse;
  }
}
