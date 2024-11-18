package com.move.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<T> {


  T buildEntity(ResultSet resultSet) throws SQLException;
}
