package com.move.service.exchange;

import com.move.context.AppContext;
import com.move.dto.CurrencyExchangeDto;
import com.move.dto.ExchangeRateDto;
import com.move.model.CurrencyExchange;
import com.move.model.ExchangeRate;

import java.math.BigDecimal;

public class CurrencyExchangeService {

  private final ExchangeRateService exchangeRateService;

  public CurrencyExchangeService() {
    this.exchangeRateService = AppContext.getInstance().getExchangeRateService();
  }

  public CurrencyExchange convertCurrency(CurrencyExchangeDto currencyExchangeDto) {
    ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCurrencyCodes(
            currencyExchangeDto.baseCurrencyCode(),
            currencyExchangeDto.targetCurrencyCode()
    );
    BigDecimal amount = currencyExchangeDto.amount();
    BigDecimal rate = exchangeRate.getRate();
    BigDecimal convertedAmount = amount.multiply(rate);

    return CurrencyExchange.builder()
            .baseCurrency(exchangeRate.getBaseCurrency())
            .targetCurrency(exchangeRate.getTargetCurrency())
            .rate(rate)
            .amount(amount)
            .convertedAmount(convertedAmount)
            .build();
  }
}
