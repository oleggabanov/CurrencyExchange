package com.move.service.exchange;

import com.move.dto.CurrencyExchangeDto;
import com.move.dto.ExchangeRateDto;
import com.move.model.CurrencyExchange;
import com.move.model.ExchangeRate;
import lombok.SneakyThrows;

import java.math.BigDecimal;

public class CurrencyExchangeService {

  private ExchangeRateService exchangeRateService;

  public CurrencyExchangeService() {
    this.exchangeRateService = new ExchangeRateService();
  }

  @SneakyThrows
  public CurrencyExchange convertCurrency(CurrencyExchangeDto currencyExchangeDto) {
    ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCurrencyCodes(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(currencyExchangeDto.baseCurrencyCode())
                    .targetCurrencyCode(currencyExchangeDto.targetCurrencyCode())
                    .build()
    );
    BigDecimal amount = currencyExchangeDto.amount();
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
