package com.move.dao;

import java.util.Optional;

public interface CurrencyDao<T, ID> extends CrudDao<T, ID> {

  Optional<T> findByCode(String code);

}
