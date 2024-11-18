package com.move.dao;

import com.move.context.AppContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {

  private final Connection connection;

  public AbstractDao() {
    this.connection = AppContext.getInstance().getConnection();
  }

  protected List<T> executeQuery(String sqlQuery,
                                 PreparedStatementSetter preparedStatementSetter,
                                 ResultSetMapper<T> mapper) {
    try (final PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      if (preparedStatementSetter != null) {
        preparedStatementSetter.setParam(preparedStatement);
      }
      try(final ResultSet resultSet = preparedStatement.executeQuery()){
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
          T entity = mapper.buildEntity(resultSet);
          list.add(entity);
        }
        return list;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


}
