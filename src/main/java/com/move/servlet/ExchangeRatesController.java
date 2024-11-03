package com.move.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.model.ExchangeRatesResponse;
import com.move.model.dao.ExchangeRatesDao;
import com.move.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    try {
      List<ExchangeRatesResponse> exchangeRates = exchangeRatesService.getAllExchangeRates(exchangeRatesDao.findExchangeRatesFromDB());
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(resp.getWriter(), exchangeRates);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


  }
}
