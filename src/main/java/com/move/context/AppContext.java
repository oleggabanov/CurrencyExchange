package com.move.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.dao.CurrencyDao;
import com.move.dao.ExchangeRateDao;
import com.move.dao.impl.CurrencyDaoJDBC;
import com.move.dao.impl.ExchangeRateDaoJDBC;
import com.move.dto.CurrencyDto;
import com.move.mapper.CustomMapper;
import com.move.model.Currency;
import lombok.Getter;

import java.sql.Connection;

@Getter
public class AppContext {

  private static AppContext instance;
  private CurrencyDao currencyDao;
  private ExchangeRateDao exchangeRateDao;
  private Connection connection;
  private ObjectMapper objectMapper;
  private CustomMapper<Currency, CurrencyDto> customMapper;

  private AppContext() {
  }

  public static AppContext getInstance() {
    if (instance == null) {
      instance = new AppContext();
    }
    return instance;
  }

  public void initialize(Connection connection) {
    this.connection = connection;
    this.objectMapper = new ObjectMapper();
    this.currencyDao = new CurrencyDaoJDBC(connection);
    this.exchangeRateDao = new ExchangeRateDaoJDBC(connection);
    this.customMapper = new CustomMapper<>();
  }

}
