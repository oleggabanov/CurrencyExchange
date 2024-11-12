package com.move.context;

import com.move.dao.CurrencyDaoJDBC;
import com.move.dao.CurrencyDao;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationContext implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext servletContext = sce.getServletContext();
    CurrencyDao currencyDao = new CurrencyDaoJDBC();
    servletContext.setAttribute("currencyDao", currencyDao);
  }

}
