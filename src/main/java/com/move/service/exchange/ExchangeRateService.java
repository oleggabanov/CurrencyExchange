package com.move.service.exchange;

import com.move.context.AppContext;
import com.move.dao.CurrencyDao;
import com.move.dao.ExchangeRateDao;
import com.move.dto.ExchangeRateDto;
import com.move.exception.EntityAlreadyExistsException;
import com.move.exception.EntityNotFoundException;
import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

  private final CurrencyDao currencyDao;
  private final ExchangeRateDao exchangeRateDao;

  public ExchangeRateService() {
    this.currencyDao = AppContext.getInstance().getCurrencyDao();
    this.exchangeRateDao = AppContext.getInstance().getExchangeRateDao();
  }

  public List<ExchangeRate> getAllExchangeRates() {
    return exchangeRateDao.findAll();
  }


  public ExchangeRate getExchangeRateByCurrencyCodes(
          String baseCurrencyCode,
          String targetCurrencyCode
  ) {
    Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
            .orElseThrow();
    Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
            .orElseThrow();
    ExchangeRate exchangeRate = findStraightOrReverseExchangeRate(baseCurrency, targetCurrency);

    if (exchangeRate == null) {
      exchangeRate = findCrossExchangeRate(baseCurrency, targetCurrency);
    }

    return exchangeRate;
  }

  private ExchangeRate findStraightOrReverseExchangeRate(
          Currency baseCurrency,
          Currency targetCurrency) {
    Optional<ExchangeRate> findByCurrencyIds = exchangeRateDao.findByCurrencyIds(baseCurrency.getId(), targetCurrency.getId());
    BigDecimal rate;
    if (findByCurrencyIds.isPresent()) {
      int id = findByCurrencyIds.get().getId();
      rate = findByCurrencyIds.get().getRate();
      return buildExchangeRate(id, baseCurrency, targetCurrency, rate);
    }

    findByCurrencyIds = exchangeRateDao.findByCurrencyIds(targetCurrency.getId(), baseCurrency.getId());
    if (findByCurrencyIds.isPresent()) {
      rate = findByCurrencyIds.get().getRate();
      rate = BigDecimal.valueOf(1).divide(rate, 7, BigDecimal.ROUND_HALF_UP);
      ExchangeRate exchangeRate = ExchangeRate.builder()
              .baseCurrency(baseCurrency)
              .targetCurrency(targetCurrency)
              .rate(rate)
              .build();
      return exchangeRateDao.save(exchangeRate);
    }
    return null;
  }

  private ExchangeRate findCrossExchangeRate(Currency baseCurrency, Currency targetCurrency) {
    ExchangeRate exchangeRate1 = null;
    ExchangeRate exchangeRate2 = null;
    BigDecimal rate;
    Currency currency1;
    Currency currency2;
    for (ExchangeRate eRate : getAllExchangeRates()) {
      currency1 = eRate.getBaseCurrency();
      currency2 = eRate.getTargetCurrency();

      if (baseCurrency.getCode().equals(currency1.getCode())) {
        exchangeRate1 = findStraightOrReverseExchangeRate(baseCurrency, currency2);
        exchangeRate2 = findStraightOrReverseExchangeRate(currency2, targetCurrency);
        break;
      }

      if (baseCurrency.getCode().equals(currency2.getCode())) {
        exchangeRate1 = findStraightOrReverseExchangeRate(baseCurrency, currency1);
        exchangeRate2 = findStraightOrReverseExchangeRate(currency1, targetCurrency);
        break;
      }

    }

    if (exchangeRate1 != null && exchangeRate2 != null) {
      rate = exchangeRate1.getRate().multiply(exchangeRate2.getRate());
    } else {
      throw new EntityNotFoundException("Обменный курс для пары не найден");
    }

    exchangeRate1 = addExchangeRate(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrency.getCode())
                    .targetCurrencyCode(targetCurrency.getCode())
                    .rate(rate)
                    .build()
    );

    return exchangeRateDao.save(exchangeRate1);
  }


  public ExchangeRate addExchangeRate(ExchangeRateDto exchangeRateDto) {
    Currency baseCurrency = currencyDao.findByCode(exchangeRateDto.baseCurrencyCode())
            .orElseThrow(() -> new EntityNotFoundException("Обменный курс не найден"));
    Currency targetCurrency = currencyDao.findByCode(exchangeRateDto.targetCurrencyCode())
            .orElseThrow(() -> new EntityNotFoundException("Обменный курс не найден"));

    Optional<ExchangeRate> byCurrencyIds = exchangeRateDao.findByCurrencyIds(baseCurrency.getId(), targetCurrency.getId());

    if (byCurrencyIds.isPresent()) {
      throw new EntityAlreadyExistsException("Валютная пара с таким кодом уже существует");
    }

    ExchangeRate exchangeRate = ExchangeRate.builder()
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(exchangeRateDto.rate())
            .build();

    return exchangeRateDao.save(exchangeRate);
  }

  public ExchangeRate updateExchangeRate(ExchangeRateDto exchangeRateDto) {
    return exchangeRateDao.findByCurrencyCodes(
                    exchangeRateDto.baseCurrencyCode(),
                    exchangeRateDto.targetCurrencyCode()).stream()
            .peek(exchangeRate -> exchangeRate.setRate(exchangeRateDto.rate()))
            .map(exchangeRateDao::save)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Валютная пара отсутствует в базе данных"));
  }

  public void deleteExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
    exchangeRateDao.delete(getExchangeRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode));
  }

  private ExchangeRate buildExchangeRate(
          int id,
          Currency baseCurrency,
          Currency targetCurrency,
          BigDecimal rate
  ) {
    return ExchangeRate.builder()
            .id(id)
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(rate)
            .build();
  }
}
