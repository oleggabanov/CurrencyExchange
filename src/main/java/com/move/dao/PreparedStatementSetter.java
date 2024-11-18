package com.move.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementSetter {

  void setParam(PreparedStatement preparedStatement) throws SQLException;

}
