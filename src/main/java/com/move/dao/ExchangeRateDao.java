package com.move.dao;

import com.move.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Integer> {

  Optional<ExchangeRate> findByCurrencyIds(int baseCurrencyId, int targetCurrencyId);

}
