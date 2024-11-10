package com.move.service;

import com.move.model.CurrencyResponse;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUtils {

  public static CurrencyResponse getCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
    return CurrencyResponse.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .fullName(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }

}
