package com.move.dao;

import com.move.model.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, Integer> {

  Optional<Currency> findByCode(String code);

}
