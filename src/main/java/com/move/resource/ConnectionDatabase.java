package com.move.resource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDatabase {

  private static Connection connection = null;

  private ConnectionDatabase() {
    try {
      URL url = ConnectionDatabase.class.getClassLoader().getResource("identifier.sqlite");
      String path = null;
      if (url != null) {
        path = new File(url.toURI()).getAbsolutePath();
      }
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    } catch (ClassNotFoundException | SQLException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static Connection getConnection() {
    if (connection == null) {
      new ConnectionDatabase();
    }
    return connection;
  }

}
