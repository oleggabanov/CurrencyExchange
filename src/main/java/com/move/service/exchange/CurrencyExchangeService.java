package com.move.service.exchange;

import com.move.dto.ExchangeRateDto;
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
    ExchangeRate exchangeRate = exchangeRatesService.getExchangeRateByCurrencyCodes(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .build()
    );
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
