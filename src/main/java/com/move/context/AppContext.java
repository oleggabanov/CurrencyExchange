package com.move.context;

import com.move.dao.CurrencyDao;
import com.move.dao.ExchangeRateDao;
import com.move.dao.impl.CurrencyDaoJDBC;
import com.move.dao.impl.ExchangeRateDaoJDBC;
import lombok.Getter;

import java.sql.Connection;

@Getter
public class AppContext {

  private static AppContext instance;
  private CurrencyDao currencyDao;
  private ExchangeRateDao exchangeRateDao;
  private Connection connection;

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
    this.currencyDao = new CurrencyDaoJDBC(connection);
    this.exchangeRateDao = new ExchangeRateDaoJDBC(connection);
  }

}
