package com.move.service.exchange;

import com.move.model.CurrencyExchange;
import com.move.model.ExchangeRate;
import lombok.SneakyThrows;

import java.math.BigDecimal;

public class CurrencyExchangeService {

  private ExchangeRatesService exchangeRatesService;

  public CurrencyExchangeService() {
    this.exchangeRatesService = new ExchangeRatesService();
  }

  @SneakyThrows
  public CurrencyExchange convertCurrency(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
    ExchangeRate exchangeRate = exchangeRatesService.getExchangeRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    BigDecimal rate = exchangeRate.getRate();
    BigDecimal convertedAmount = amount.multiply(rate);

    CurrencyExchange currencyExchange = CurrencyExchange.builder()
            .baseCurrency(exchangeRate.getBaseCurrency())
            .targetCurrency(exchangeRate.getTargetCurrency())
            .rate(rate)
            .amount(amount)
            .convertedAmount(convertedAmount)
            .build();

    return currencyExchange;
  }
}
