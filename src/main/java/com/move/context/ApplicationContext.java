package com.move.context;

import com.move.resource.ConnectionDatabase;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;

@WebListener
public class ApplicationContext implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Connection connection = ConnectionDatabase.getConnection();
    AppContext.getInstance().initialize(connection);
  }

}
